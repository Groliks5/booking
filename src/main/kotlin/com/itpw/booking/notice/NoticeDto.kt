package com.itpw.booking.notice

import com.fasterxml.jackson.annotation.JsonProperty
import com.itpw.booking.additional_feature.AdditionalFeatureResponse
import com.itpw.booking.condition.ConditionResponse
import com.itpw.booking.user.UserShortInfoResponse
import jakarta.persistence.*
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateNoticeRequest(
    @JsonProperty("title")
    @field:Size(min = 0, max = 45, message = "{notice_title_length_error}")
    val title: String,
    @JsonProperty("address")
    @field:NotBlank(message = "{empty_address_error}")
    val address: String,
    @JsonProperty("rooms_count")
    val roomsCount: RoomsCount,
    @JsonProperty("price_per_day")
    @field:Min(0, message = "{price_error}")
    val pricePerDay: Double,
    @JsonProperty("price_per_hour")
    @field:Min(0, message = "{price_error}")
    val pricePerHour: Double?,
    @JsonProperty("price_per_night")
    @field:Min(0, message = "{price_error}")
    val pricePerNight: Double?,
    @JsonProperty("floor")
    @field:Min(-1, message = "{floor_error}")
    val floor: Int?,
    @JsonProperty("max_floor_in_entrance")
    @field:Min(1, message = "{floor_error}")
    val maxFloorInEntrance: Int?,
    @JsonProperty("square")
    @field:Min(0, message = "{square_error}")
    val square: Double?,
    @JsonProperty("extra_info")
    @field:Size(min = 0, max = 10_000, message = "{extra_info_length_error}")
    val extraInfo: String?,
    @JsonProperty("longitude")
    @field:Min(-180, message = "{longitude_error}")
    @field:Max(180, message = "{longitude_error}")
    val longitude: Double,
    @JsonProperty("latitude")
    @field:Min(-90, message = "{latitude_error}")
    @field:Max(90, message = "{latitude_error}")
    val latitude: Double,
    @JsonProperty("images")
    @field:Size(min = 1, message = "{notice_images_min_error}")
    @field:Valid
    val images: List<NoticeImageRequest>,
    @JsonProperty("deposit")
    val deposit: ConditionType?,
    @JsonProperty("pre_payment")
    val prePayment: ConditionType?,
    @JsonProperty("selected_conditions")
    val selectedConditions: List<Long>,
    @JsonProperty("selected_additional_features")
    val selectedAdditionalFeatures: List<Long>,
)

data class NoticeImageRequest(
    @JsonProperty("href")
    val href: String,
    @JsonProperty("position")
    @field:Min(0, message = "{wrong_position_error}")
    val position: Int
)

data class NoticeCountResponse(
    @JsonProperty("count")
    val count: Long
)

data class NoticeResponse(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("title")
    val title: String,
    @JsonProperty("address")
    val address: String,
    @JsonProperty("rooms_count")
    val roomsCount: RoomsCount,
    @JsonProperty("price_per_day")
    val pricePerDay: Double,
    @JsonProperty("price_per_hour")
    val pricePerHour: Double?,
    @JsonProperty("price_per_night")
    val pricePerNight: Double?,
    @JsonProperty("floor")
    val floor: Int?,
    @JsonProperty("max_floor_in_entrance")
    val maxFloorInEntrance: Int?,
    @JsonProperty("square")
    val square: Double?,
    @JsonProperty("extra_info")
    val extraInfo: String?,
    @JsonProperty("longitude")
    val longitude: Double,
    @JsonProperty("latitude")
    val latitude: Double,
    @JsonProperty("images")
    val images: List<String>,
    @JsonProperty("deposit")
    val deposit: ConditionType?,
    @JsonProperty("pre_payment")
    val prePayment: ConditionType?,
    @JsonProperty("selected_conditions")
    val selectedConditions: List<Long>,
    @JsonProperty("selected_additional_features")
    val selectedAdditionalFeatures: List<Long>,
    @JsonProperty("metro_station")
    val metroStation: String?
) {
    constructor(notice: Notice): this(
        id = notice.id,
        title = notice.title,
        address = notice.address,
        roomsCount = notice.roomsCount,
        pricePerDay = notice.pricePerDay,
        pricePerHour = notice.pricePerHour,
        pricePerNight = notice.pricePerNight,
        floor = notice.floor,
        maxFloorInEntrance = notice.maxFloorInEntrance,
        square = notice.square,
        extraInfo = notice.extraInfo,
        longitude = notice.longitude,
        latitude = notice.latitude,
        images = notice.images.map { it.href },
        deposit = notice.deposit,
        prePayment = notice.prePayment,
        selectedConditions = notice.selectedConditions.map { it.id },
        selectedAdditionalFeatures = notice.selectedAdditionalFeatures.map { it.id },
        metroStation = notice.metro?.title
    )
}

data class NoticeViewResponse(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("title")
    val title: String,
    @JsonProperty("address")
    val address: String,
    @JsonProperty("rooms_count")
    val roomsCount: RoomsCount,
    @JsonProperty("price_per_day")
    val pricePerDay: Double,
    @JsonProperty("price_per_hour")
    val pricePerHour: Double?,
    @JsonProperty("price_per_night")
    val pricePerNight: Double?,
    @JsonProperty("floor")
    val floor: Int?,
    @JsonProperty("max_floor_in_entrance")
    val maxFloorInEntrance: Int?,
    @JsonProperty("square")
    val square: Double?,
    @JsonProperty("extra_info")
    val extraInfo: String?,
    @JsonProperty("longitude")
    val longitude: Double,
    @JsonProperty("latitude")
    val latitude: Double,
    @JsonProperty("images")
    val images: List<String>,
    @JsonProperty("deposit")
    val deposit: ConditionType?,
    @JsonProperty("pre_payment")
    val prePayment: ConditionType?,
    @JsonProperty("selected_conditions")
    val selectedConditions: List<ConditionResponse>,
    @JsonProperty("selected_additional_features")
    val selectedAdditionalFeatures: List<AdditionalFeatureResponse>,
    @JsonProperty("user")
    val user: UserShortInfoResponse,
    @JsonProperty("metro_station")
    val metroStation: String?
) {
    constructor(notice: Notice): this(
        id = notice.id,
        title = notice.title,
        address = notice.address,
        roomsCount = notice.roomsCount,
        pricePerDay = notice.pricePerDay,
        pricePerHour = notice.pricePerHour,
        pricePerNight = notice.pricePerNight,
        floor = notice.floor,
        maxFloorInEntrance = notice.maxFloorInEntrance,
        square = notice.square,
        extraInfo = notice.extraInfo,
        longitude = notice.longitude,
        latitude = notice.latitude,
        images = notice.images.map { it.href },
        deposit = notice.deposit,
        prePayment = notice.prePayment,
        selectedConditions = notice.selectedConditions.map { ConditionResponse(it) },
        selectedAdditionalFeatures = notice.selectedAdditionalFeatures.map { AdditionalFeatureResponse(it) },
        user = UserShortInfoResponse(notice.creator),
        metroStation = notice.metro?.title
    )
}

data class ShortNoticeViewResponse(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("price")
    val price: Double,
    @JsonProperty("images")
    val images: List<String>,
    @JsonProperty("title")
    val title: String,
    @JsonProperty("longitude")
    val longitude: Double,
    @JsonProperty("latitude")
    val latitude: Double,
    @JsonProperty("address")
    val address: String,
    @JsonProperty("rooms_count")
    val roomsCount: RoomsCount,
    @JsonProperty("user")
    val user: UserShortInfoResponse,
    @JsonProperty("metro_station")
    val metroStation: String?
) {
    constructor(notice: Notice, period: NoticePeriod): this(
        id = notice.id,
        title = notice.title,
        address = notice.address,
        roomsCount = notice.roomsCount,
        price = notice.getPriceForPeriod(period),
        longitude = notice.longitude,
        latitude = notice.latitude,
        images = notice.images.map { it.href },
        user = UserShortInfoResponse(notice.creator),
        metroStation = notice.metro?.title
    )
}

data class ShortNoticeResponse(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("images")
    val images: List<String>,
    @JsonProperty("title")
    val title: String,
    @JsonProperty("longitude")
    val longitude: Double,
    @JsonProperty("latitude")
    val latitude: Double,
    @JsonProperty("address")
    val address: String,
    @JsonProperty("rooms_count")
    val roomsCount: RoomsCount,
    @JsonProperty("user")
    val user: UserShortInfoResponse,
    @JsonProperty("price_per_day")
    val pricePerDay: Double,
    @JsonProperty("price_per_hour")
    val pricePerHour: Double?,
    @JsonProperty("price_per_night")
    val pricePerNight: Double?,
    @JsonProperty("metro_station")
    val metroStation: String?
) {
    constructor(notice: Notice): this(
        id = notice.id,
        title = notice.title,
        address = notice.address,
        roomsCount = notice.roomsCount,
        pricePerDay = notice.pricePerDay,
        pricePerHour = notice.pricePerHour,
        pricePerNight = notice.pricePerNight,
        longitude = notice.longitude,
        latitude = notice.latitude,
        images = notice.images.map { it.href },
        user = UserShortInfoResponse(notice.creator),
        metroStation = notice.metro?.title
    )
}

data class NoticeInMapResponse(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("price")
    val price: Double,
    @JsonProperty("rooms_count")
    val roomsCount: RoomsCount,
    @JsonProperty("latitude")
    val latitude: Double,
    @JsonProperty("longitude")
    val longitude: Double
) {
    constructor(notice: Notice, period: NoticePeriod): this(
        id = notice.id,
        roomsCount = notice.roomsCount,
        price = notice.getPriceForPeriod(period),
        longitude = notice.longitude,
        latitude = notice.latitude,
    )
}