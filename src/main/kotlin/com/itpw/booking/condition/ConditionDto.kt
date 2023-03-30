package com.itpw.booking.condition

import com.fasterxml.jackson.annotation.JsonProperty

data class ConditionResponse(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("title")
    val title: String
) {
    constructor(condition: Condition): this(
        id = condition.id,
        title = condition.title
    )
}