package com.itpw.booking.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "yandex")
data class YandexProperties(
    var apiKey: String,
)