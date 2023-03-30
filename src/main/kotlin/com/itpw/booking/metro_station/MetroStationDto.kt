package com.itpw.booking.metro_station

import com.fasterxml.jackson.annotation.JsonProperty

data class MetroStationResponse(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("title")
    val title: String
) {
    constructor(metroStation: MetroStation): this(
        id = metroStation.id,
        title = metroStation.title
    )
}