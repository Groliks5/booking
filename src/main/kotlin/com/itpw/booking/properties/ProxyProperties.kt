package com.itpw.booking.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "proxy-settings")
data class ProxyProperties(
    var uri: String
)