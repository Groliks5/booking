package com.itpw.booking.metro_station

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/metro_station")
class MetroStationController @Autowired constructor(
    private val metroStationService: MetroStationService
) {
    @GetMapping("")
    fun getMetroStations(

    ): List<MetroStationResponse> {
        val metroStations = metroStationService.getAvailableMetroStations()
        return metroStations.map { MetroStationResponse(it) }
    }
}