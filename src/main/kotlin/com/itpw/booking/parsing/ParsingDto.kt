package com.itpw.booking.parsing

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.web.multipart.MultipartFile

data class ParseFileRequest(
    val file: MultipartFile
)

data class CreateUserCreditianals(
    val login: String,
    val password: String
)