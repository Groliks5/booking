package com.itpw.booking.notice

import org.springframework.data.domain.Page

interface CustomNoticeRepository {
    fun findNoticesWithFilters(
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
        page: Int, pageSize: Int
    ): Page<Notice>

    fun findNoticesCountWithFilters(
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
    ): Long
}