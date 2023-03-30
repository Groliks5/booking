package com.itpw.booking.additional_feature

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/additional_feature")
class AdditionalFeatureController @Autowired constructor(
    private val additionalFeatureService: AdditionalFeatureService
) {
    @GetMapping("")
    fun getAdditionalFeatures(): List<AdditionalFeatureResponse> {
        val additionalFeatures = additionalFeatureService.getAdditionalFeatures()
        return additionalFeatures.map { AdditionalFeatureResponse(it) }
    }
}