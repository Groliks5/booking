package com.itpw.booking.additional_feature

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AdditionalFeatureService @Autowired constructor(
    private val additionalFeatureRepository: AdditionalFeatureRepository
) {
    fun getAdditionalFeatures(): List<AdditionalFeature> {
        return additionalFeatureRepository.findAll().toList()
    }

    fun getOrCreateAdditionalFeature(featureTitle: String): AdditionalFeature {
        val feature = additionalFeatureRepository.findByTitleIgnoreCase(featureTitle)
        if (feature != null) {
            return feature
        } else {
            return additionalFeatureRepository.save(AdditionalFeature(title = featureTitle))
        }
    }
}