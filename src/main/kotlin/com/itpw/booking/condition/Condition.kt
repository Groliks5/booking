package com.itpw.booking.condition

import com.itpw.booking.notice.Notice
import jakarta.persistence.*

@Entity
@Table(
    name = "conditions"
)
class Condition (
    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY,
    )
    val id: Long = -1L,
    var title: String,
    @ManyToMany(mappedBy = "selectedConditions")
    val notices: List<Notice> = listOf()
)