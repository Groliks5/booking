package com.itpw.booking.notice

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.Query
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

class CustomNoticeRepositoryImpl : CustomNoticeRepository {
    @PersistenceContext
    lateinit var entityManager: EntityManager

    override fun findNoticesWithFilters(
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
        pageSize: Int
    ): Page<Notice> {
        val pageable = PageRequest.of(page, pageSize)
        val results = createFindNoticesQuery(
            false,
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
            .setFirstResult(page * pageSize)
            .setMaxResults(pageSize)
            .resultList as List<Notice>
        val count = createFindNoticesQuery(
            true,
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
            .singleResult as Long
        return PageImpl<Notice>(results, pageable, count)
    }

    override fun findNoticesCountWithFilters(
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
        metroStations: List<Long>?
    ): Long {
        return createFindNoticesQuery(
            true,
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
            .singleResult as Long
    }

    private fun createFindNoticesQuery(
        isCountQuery: Boolean,
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
    ): Query {
        val queryStart = if (isCountQuery) "SELECT count(n.id)" else ""
        val priceType = when (noticePeriod) {
            NoticePeriod.DAY -> "pricePerDay"
            NoticePeriod.NIGHT -> "pricePerNight"
            NoticePeriod.HOUR -> "pricePerHour"
        }
        val roomsCountCondition = if (roomsCount == null) "" else " AND n.roomsCount IN :rooms_count"
        val withoutDepositCondition = if (!isWithoutDeposit) "" else " AND n.deposit = :deposit"
        val withoutPrePaymentCondition = if (!isWithoutPrePayment) "" else " AND n.prePayment = :pre_payment"
        val additionalFeaturesJoin = if (additionalFeatures == null) "" else " JOIN n.selectedAdditionalFeatures saf"
        val additionalFeaturesCondition = if (additionalFeatures == null) "" else " AND saf.id IN :additional_features"
        val squareFromCondition = if (squareFrom == null) "" else " AND n.square >= :square_from"
        val squareToCondition = if (squareTo == null) "" else " AND n.square <= :square_to"
        val conditionsJoin = if (conditions == null) "" else " JOIN n.selectedConditions sc"
        val conditionsCondition = if (conditions == null) "" else " AND sc.id IN :selected_conditions"
        val metroStationJoin = if (metroStations == null) "" else " JOIN n.metro m"
        val metroStationCondition = if (metroStations == null) "" else " AND m.id IN :metro_stations"
        val ordering = if (isCountQuery) {
            ""
        } else {
            when (ordering) {
                NoticeOrdering.DEFAULT -> " ORDER BY n.title ASC"
                NoticeOrdering.CHIP_FIRST -> " ORDER BY n.$priceType ASC"
                NoticeOrdering.EXPENSIVE_FIRST -> " ORDER BY n.$priceType DESC"
            }
        }
        val query =
            entityManager.createQuery("$queryStart FROM Notice n$additionalFeaturesJoin$conditionsJoin$metroStationJoin WHERE n.$priceType BETWEEN :price_from AND :price_to AND floor BETWEEN :floor_from AND :floor_to$roomsCountCondition$withoutDepositCondition$withoutPrePaymentCondition$additionalFeaturesCondition$squareFromCondition$squareToCondition$conditionsCondition$metroStationCondition$ordering")
                .setParameter("price_from", priceFrom)
                .setParameter("price_to", priceTo)
                .setParameter("floor_from", floorFrom)
                .setParameter("floor_to", floorTo)
        if (roomsCount != null) {
            query.setParameter("rooms_count", roomsCount)
        }
        if (isWithoutDeposit) {
            query.setParameter("deposit", ConditionType.NO)
        }
        if (isWithoutPrePayment) {
            query.setParameter("pre_payment", ConditionType.NO)
        }
        if (additionalFeatures != null) {
            query.setParameter("additional_features", additionalFeatures)
        }
        if (squareFrom != null) {
            query.setParameter("square_from", squareFrom)
        }
        if (squareTo != null) {
            query.setParameter("square_to", squareTo)
        }
        if (conditions != null) {
            query.setParameter("selected_conditions", conditions)
        }
        if (metroStations != null) {
            query.setParameter("metro_stations", metroStations)
        }
        return query
    }
}