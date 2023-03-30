package com.itpw.booking.additional_feature

import jakarta.persistence.*

@Entity
@Table(
    name = "additional_features"
)
class AdditionalFeature(
    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY,
    )
    val id: Long = -1L,
    var title: String,
)