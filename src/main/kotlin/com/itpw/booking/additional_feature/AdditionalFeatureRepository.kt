package com.itpw.booking.additional_feature

import org.springframework.data.repository.CrudRepository

interface AdditionalFeatureRepository: CrudRepository<AdditionalFeature, Long> {
    fun findByTitleIgnoreCase(title: String): AdditionalFeature?
}