package com.itpw.booking.media

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.multipart.MultipartFile
import java.beans.ConstructorProperties

data class UploadImageRequest @ConstructorProperties("image") constructor(
    val images: List<MultipartFile>,
)

data class ResourceHrefResponse(
    @JsonProperty("href")
    val href: String
)