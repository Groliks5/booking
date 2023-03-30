package com.itpw.booking.parsing

import com.itpw.booking.notice.ConditionType
import com.itpw.booking.notice.RoomsCount

data class ParsedUser(
    val name: String,
    val phone: String,
    val viber: String?,
    val whatsApp: String?,
    val vk: String?,
    val telegram: String?,
    val notices: MutableList<ParsedNotice> = mutableListOf()
)

data class ParsedNotice(
    val title: String,
    val address: String,
    val roomsCount: RoomsCount,
    val description: String,
    val deposit: ConditionType?,
    val prePayment: ConditionType?,
    val longitude: Double,
    val latitude: Double,
    val pricePerDay: Double,
    val pricePerHour: Double?,
    val pricePerNight: Double?,
    val images: List<String>,
    val additionalFeatures: List<String>,
    val conditions: List<String>,
    val square: Double?,
    val floor: Int?,
    val maxFloor: Int?,
)