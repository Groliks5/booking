package com.itpw.booking.condition

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
    var title: String
)