package com.itpw.booking.notice

import com.itpw.booking.util.DetailsResponse
import com.itpw.booking.util.PagingFormatter
import com.itpw.booking.util.PagingResponse
import com.itpw.booking.util.Translator
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/notice")
class NoticeController @Autowired constructor(
    private val noticeService: NoticeService,
    private val pagingFormatter: PagingFormatter,
    private val translator: Translator
) {
    @GetMapping("")
    fun getUserNotice(
        authentication: Authentication,
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("page_size", defaultValue = "30") pageSize: Int,
    ): PagingResponse<ShortNoticeResponse> {
        val notices = noticeService.getUserNotices(authentication.name.toLong(), page, pageSize)
        return pagingFormatter.formPagingResponse(
            url = "notice",
            page = notices,
            mapper = {
                ShortNoticeResponse(it)
            }
        )
    }

    @GetMapping("/{id}")
    fun getNoticeForEdit(
        authentication: Authentication,
        @PathVariable("id") noticeId: Long
    ): NoticeResponse {
        val notice = noticeService.getNoticeAndCheckPermission(authentication.name.toLong(), noticeId)
        return NoticeResponse(notice)
    }

    @GetMapping("/{id}/view")
    fun getNoticeForView(
        @PathVariable("id") noticeId: Long
    ): NoticeViewResponse {
        val notice = noticeService.getNotice(noticeId)
        return NoticeViewResponse(notice)
    }

    @GetMapping("/view")
    fun getNoticesForView(
        @RequestParam("ordering") ordering: NoticeOrdering,
        @RequestParam("notice_period") noticePeriod: NoticePeriod,
        @RequestParam("price_from") priceFrom: Double,
        @RequestParam("price_to") priceTo: Double,
        @RequestParam("rooms_count") roomsCount: RoomsCount?,
        @RequestParam("floor_from") floorFrom: Int,
        @RequestParam("floor_to") floorTo: Int,
        @RequestParam("is_without_deposit", defaultValue = "false") isWithoutDeposit: Boolean,
        @RequestParam("is_without_pre_payment", defaultValue = "false") isWithoutPrePayment: Boolean,
        @RequestParam("additional_features") additionalFeatures: List<Long>?,
        @RequestParam("square_from", defaultValue = "0") squareFrom: Int?,
        @RequestParam("square_to", defaultValue = "1000") squareTo: Int?,
        @RequestParam("conditions") conditions: List<Long>?,
        @RequestParam("metro_stations") metroStations: List<Long>?,
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("page_size", defaultValue = "30") pageSize: Int,
    ): PagingResponse<ShortNoticeResponse> {
        val notices = noticeService.getNotices(
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
        return pagingFormatter.formPagingResponse(
            url = "notice/view",
            queryParams = buildMap {
                put("ordering", ordering.toString())
                put("notice_period", noticePeriod.toString())
                put("price_from", priceFrom.toString())
                put("price_to", priceTo.toString())
                if (roomsCount != null) {
                    put("rooms_count", roomsCount.toString())
                }
                put("floor_from", floorFrom.toString())
                put("floor_to", floorTo.toString())
                put("is_without_deposit", isWithoutDeposit.toString())
                put("is_without_pre_payment", isWithoutPrePayment.toString())
                if (additionalFeatures != null) {
                    put("additional_features", additionalFeatures.joinToString("&additional_features="))
                }
                put("square_from", squareFrom.toString())
                put("square_to", squareTo.toString())
                if (metroStations != null) {
                    put("metro_stations", metroStations.joinToString("&metro_stations="))
                }
                if (conditions != null) {
                    put("conditions", conditions.joinToString("&conditions="))
                }
            },
            page = notices,
            mapper = {
                ShortNoticeResponse(it)
            }
        )
    }

    @GetMapping("/view/map")
    fun getNoticesForViewInMap(
        @RequestParam("ordering") ordering: NoticeOrdering,
        @RequestParam("notice_period") noticePeriod: NoticePeriod,
        @RequestParam("price_from") priceFrom: Double,
        @RequestParam("price_to") priceTo: Double,
        @RequestParam("rooms_count") roomsCount: RoomsCount?,
        @RequestParam("floor_from") floorFrom: Int,
        @RequestParam("floor_to") floorTo: Int,
        @RequestParam("is_without_deposit", defaultValue = "false") isWithoutDeposit: Boolean,
        @RequestParam("is_without_pre_payment", defaultValue = "false") isWithoutPrePayment: Boolean,
        @RequestParam("additional_features") additionalFeatures: List<Long>?,
        @RequestParam("conditions") conditions: List<Long>?,
        @RequestParam("metro_stations") metroStations: List<Long>?,
        @RequestParam("square_from", defaultValue = "0") squareFrom: Int?,
        @RequestParam("square_to", defaultValue = "1000") squareTo: Int?
    ): List<NoticeInMapResponse> {
        val notices = noticeService.getNotices(
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
            0,
            1_000_000
        ).content
        return notices.map { NoticeInMapResponse(it, noticePeriod) }
    }

    @GetMapping("/view/by_ids")
    fun getNoticesForViewByIds(
        @RequestParam("id") ids: List<Long>,
        @RequestParam("notice_period") noticePeriod: NoticePeriod,
    ): List<ShortNoticeViewResponse> {
        val notices = noticeService.getNotices(ids)
        return notices.map { ShortNoticeViewResponse(it, noticePeriod) }
    }

    @GetMapping("/view/{id}/details")
    fun getNoticeDetailsForView(
        @PathVariable("id") id: Long
    ): NoticeViewResponse {
        val notice = noticeService.getNotice(id)
        return NoticeViewResponse(notice)
    }

    @GetMapping("/view/count")
    fun getNoticesCountForView(
        @RequestParam("ordering") ordering: NoticeOrdering,
        @RequestParam("notice_period") noticePeriod: NoticePeriod,
        @RequestParam("price_from") priceFrom: Double,
        @RequestParam("price_to") priceTo: Double,
        @RequestParam("rooms_count") roomsCount: RoomsCount?,
        @RequestParam("floor_from") floorFrom: Int,
        @RequestParam("floor_to") floorTo: Int,
        @RequestParam("is_without_deposit", defaultValue = "false") isWithoutDeposit: Boolean,
        @RequestParam("is_without_pre_payment", defaultValue = "false") isWithoutPrePayment: Boolean,
        @RequestParam("additional_features") additionalFeatures: List<Long>?,
        @RequestParam("square_from", defaultValue = "0") squareFrom: Int?,
        @RequestParam("square_to", defaultValue = "1000") squareTo: Int?,
        @RequestParam("conditions") conditions: List<Long>?,
        @RequestParam("metro_stations") metroStations: List<Long>?,
    ): NoticeCountResponse {
        return NoticeCountResponse(
            noticeService.getNoticesCount(
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
        )
    }

    @PostMapping("")
    fun createNotice(
        authentication: Authentication,
        @Valid @RequestBody request: CreateNoticeRequest
    ): NoticeResponse {
        val notice = noticeService.createNotice(authentication.name.toLong(), request)
        return NoticeResponse(notice)
    }

    @PutMapping("/{id}")
    fun editNotice(
        authentication: Authentication,
        @PathVariable("id") noticeId: Long,
        @Valid @RequestBody request: CreateNoticeRequest
    ): NoticeResponse {
        val notice = noticeService.editNotice(authentication.name.toLong(), noticeId, request)
        return NoticeResponse(notice)
    }

    @DeleteMapping("/{id}")
    fun deleteNotice(
        authentication: Authentication,
        @PathVariable("id") noticeId: Long
    ): DetailsResponse {
        noticeService.deleteNotice(authentication.name.toLong(), noticeId)
        return DetailsResponse(translator.toLocale("notice_deleted"))
    }

//    @PostMapping("/find_metro")
//    fun findMetro(
//        @RequestParam("count") count: Int
//    ): Int {
//        return noticeService.findMetro(count)
//    }
}