package com.itpw.booking.notice

import com.itpw.booking.additional_feature.AdditionalFeatureRepository
import com.itpw.booking.condition.ConditionRepository
import com.itpw.booking.exceptions.ForbiddenException
import com.itpw.booking.exceptions.NotFoundException
import com.itpw.booking.media.FilesUploadService
import com.itpw.booking.metro_station.MetroStationService
import com.itpw.booking.notice_image.NoticeImage
import com.itpw.booking.notice_image.NoticeImageRepository
import com.itpw.booking.user.UserRepository
import com.itpw.booking.util.Translator
import com.itpw.booking.util.equalsDelta
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestParam

@Service
class NoticeService @Autowired constructor(
    private val noticeRepository: NoticeRepository,
    private val userRepository: UserRepository,
    private val conditionRepository: ConditionRepository,
    private val additionalFeatureRepository: AdditionalFeatureRepository,
    private val noticeImageRepository: NoticeImageRepository,
    private val filesUploadService: FilesUploadService,
    private val translator: Translator,
    private val metroStationService: MetroStationService
) {
    fun getNotice(noticeId: Long): Notice {
        return noticeRepository.findByIdOrNull(noticeId)
            ?: throw NotFoundException(translator.toLocale("notice_not_found"))
    }

    fun getNoticeAndCheckPermission(userId: Long, noticeId: Long): Notice {
        val user =
            userRepository.findByIdOrNull(userId) ?: throw NotFoundException(translator.toLocale("user_not_found"))
        val notice = noticeRepository.findByIdOrNull(noticeId)
            ?: throw NotFoundException(translator.toLocale("notice_not_found"))
        if (notice.creator.id != userId) {
            throw ForbiddenException(translator.toLocale("access_denied"))
        }
        return notice
    }

    fun getUserNotices(userId: Long, page: Int, pageSize: Int): Page<Notice> {
        val pageable = PageRequest.of(page, pageSize, Sort.by("title"))
        return noticeRepository.findByCreator_Id(userId, pageable)
    }

    fun getNotices(
        ordering: NoticeOrdering,
        noticePeriod: NoticePeriod,
        priceFrom: Double,
        priceTo: Double,
        roomsCount: RoomsCount?,
        floorFrom: Int,
        floorTo: Int,
        isWithoutDeposit: Boolean,
        isWithoutPrePayment: Boolean,
        additionalFeatures: List<Long>?,
        squareFrom: Int?,
        squareTo: Int?,
        conditions: List<Long>?,
        metroStations: List<Long>?,
        page: Int,
        pageSize: Int,
    ): Page<Notice> {
        return noticeRepository.findNoticesWithFilters(
            ordering,
            noticePeriod,
            priceFrom,
            priceTo,
            roomsCount,
            floorFrom,
            floorTo,
            isWithoutDeposit,
            isWithoutPrePayment,
            additionalFeatures,
            squareFrom,
            squareTo,
            conditions,
            metroStations,
            page,
            pageSize
        )
    }

    fun getNoticesCount(
        ordering: NoticeOrdering,
        noticePeriod: NoticePeriod,
        priceFrom: Double,
        priceTo: Double,
        roomsCount: RoomsCount?,
        floorFrom: Int,
        floorTo: Int,
        isWithoutDeposit: Boolean,
        isWithoutPrePayment: Boolean,
        additionalFeatures: List<Long>?,
        squareFrom: Int?,
        squareTo: Int?,
        conditions: List<Long>?,
        metroStations: List<Long>?,
    ): Long {
        return noticeRepository.findNoticesCountWithFilters(
            ordering,
            noticePeriod,
            priceFrom,
            priceTo,
            roomsCount,
            floorFrom,
            floorTo,
            isWithoutDeposit,
            isWithoutPrePayment,
            additionalFeatures,
            squareFrom,
            squareTo,
            conditions,
            metroStations
        )
    }

    fun getNotices(ids: List<Long>): List<Notice> {
        return noticeRepository.findByIdIn(ids)
    }

    @Transactional(rollbackOn = [Exception::class])
    fun createNotice(userId: Long, request: CreateNoticeRequest): Notice {
        val user =
            userRepository.findByIdOrNull(userId) ?: throw NotFoundException(translator.toLocale("user_not_found"))
        val newNotice = noticeRepository.save(
            Notice(
                title = request.title,
                address = request.address,
                creator = user,
                deposit = request.deposit,
                extraInfo = request.extraInfo,
                floor = request.floor,
                maxFloorInEntrance = request.maxFloorInEntrance,
                latitude = request.latitude,
                longitude = request.longitude,
                prePayment = request.prePayment,
                selectedConditions = conditionRepository.findAllById(request.selectedConditions).toMutableList(),
                selectedAdditionalFeatures = additionalFeatureRepository.findAllById(request.selectedAdditionalFeatures)
                    .toMutableList(),
                pricePerDay = request.pricePerDay,
                pricePerNight = request.pricePerNight,
                pricePerHour = request.pricePerHour,
                roomsCount = request.roomsCount,
                square = request.square,
                metro = metroStationService.getMetroStation(request.longitude, request.latitude),
            )
        )
        val images = noticeImageRepository.saveAll(
            request.images.sortedBy { it.position }.mapIndexed { index, noticeImageRequest ->
                NoticeImage(
                    href = noticeImageRequest.href,
                    notice = newNotice,
                    position = index
                )
            }
        ).toMutableList()
        newNotice.images = images
        return noticeRepository.save(newNotice)
    }

    @Transactional(rollbackOn = [Exception::class])
    fun editNotice(userId: Long, noticeId: Long, request: CreateNoticeRequest): Notice {
        val notice = getNoticeAndCheckPermission(userId, noticeId)
        notice.title = request.title
        notice.address = request.address
        notice.deposit = request.deposit
        notice.extraInfo = request.extraInfo
        notice.floor = request.floor
        notice.maxFloorInEntrance = request.maxFloorInEntrance
        notice.latitude = request.latitude
        notice.longitude = request.longitude
        notice.prePayment = request.prePayment
        notice.selectedConditions = conditionRepository.findAllById(request.selectedConditions).toMutableList()
        notice.selectedAdditionalFeatures =
            additionalFeatureRepository.findAllById(request.selectedAdditionalFeatures)
                .toMutableList()
        notice.pricePerDay = request.pricePerDay
        notice.pricePerNight = request.pricePerNight
        notice.pricePerHour = request.pricePerHour
        notice.roomsCount = request.roomsCount
        notice.square = request.square
        if (!notice.longitude.equalsDelta(request.longitude) || !notice.latitude.equalsDelta(request.latitude)) {
            notice.metro = metroStationService.getMetroStation(request.longitude, request.latitude)
        }
        val oldImages = notice.images.map { it.href }
        val imagesForDelete = oldImages.filterNot { oldImage -> request.images.any { it.href == oldImage } }
        val images = request.images.sortedBy {
            it.position
        }.mapIndexed { index, noticeImageRequest ->
            if (oldImages.contains(noticeImageRequest.href)) {
                noticeImageRepository.findByHref(noticeImageRequest.href)?.apply {
                    position = index
                } ?: throw NotFoundException(translator.toLocale("notice_image_not_found"))
            } else {
                noticeImageRepository.save(
                    NoticeImage(
                        href = noticeImageRequest.href,
                        position = index,
                        notice = notice
                    )
                )
            }
        }
        notice.images = noticeImageRepository.saveAll(images).toMutableList()
        val savedNotice = noticeRepository.save(notice)
        imagesForDelete.forEach {
            filesUploadService.deleteFile(it)
            noticeImageRepository.deleteByHref(it)
        }
        return savedNotice
    }

    fun deleteNotice(userId: Long, noticeId: Long) {
        val notice = getNoticeAndCheckPermission(userId, noticeId)
        notice.images.forEach {
            filesUploadService.deleteFile(it.href)
        }
        noticeRepository.delete(notice)
    }

    fun findMetro(count: Int): Int {
        val notices = noticeRepository.findByMetroIsNull()
        var usedCount = 0
        notices.forEach {
            if (usedCount < count) {
                it.metro = metroStationService.getMetroStation(it.longitude, it.latitude)
            } else {
                return@forEach
            }
            usedCount++
        }
        noticeRepository.saveAll(notices)
        return usedCount
    }
}