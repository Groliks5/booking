package com.itpw.booking.metro_station

import org.springframework.data.repository.CrudRepository

interface MetroStationRepository: CrudRepository<MetroStation, Long> {
    fun findByTitleIgnoreCase(metroName: String): MetroStation?
}