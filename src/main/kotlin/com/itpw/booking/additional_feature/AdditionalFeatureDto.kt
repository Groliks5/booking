package com.itpw.booking.additional_feature

import com.fasterxml.jackson.annotation.JsonProperty

data class AdditionalFeatureResponse(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("title")
    val title: String
) {
    constructor(additionalFeature: AdditionalFeature): this(
        id = additionalFeature.id,
        title = additionalFeature.title
    )
}