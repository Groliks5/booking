package com.itpw.booking.additional_feature

import com.itpw.booking.notice.Notice
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
    @ManyToMany(mappedBy = "selectedAdditionalFeatures")
    val notices: List<Notice> = listOf()
)