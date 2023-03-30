package com.itpw.booking.metro_station

import jakarta.persistence.*

@Entity
@Table(
    name = "metro"
)
class MetroStation (
    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY,
    )
    val id: Long = -1L,
    var title: String,
)