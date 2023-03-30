package com.itpw.booking.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "file")
data class FileStorageProperties(
    var uploadDir: String,
)