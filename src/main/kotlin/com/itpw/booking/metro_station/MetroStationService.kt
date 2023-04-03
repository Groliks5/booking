package com.itpw.booking.metro_station

import com.itpw.booking.exceptions.NotFoundException
import com.itpw.booking.properties.YandexProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.util.logging.Logger

@Service
class MetroStationService @Autowired constructor(
    private val yandexProperties: YandexProperties,
    private val metroStationRepository: MetroStationRepository
) {
    private val restTemplate = RestTemplate()

    fun getMetro(metroId: Long): MetroStation {
        return metroStationRepository.findByIdOrNull(metroId) ?: throw NotFoundException("Станция метро не найдена")
    }

    fun getMetroStation(longitude: Double, latitude: Double): MetroStation? {
        return try {
            val response = restTemplate.exchange<GeocoderResponse>("https://geocode-maps.yandex.ru/1.x/?apikey=${yandexProperties.apiKey}&geocode=$longitude, $latitude&format=json&kind=metro&results=1", HttpMethod.GET).body!!
            val metroName = response.response.geoObjectCollection.featureMember.first().geoObject.metaDataProperty.geocoderMetaData.address.components.filter { it.kind == "metro" }.map { it.name.split(' ').drop(1).joinToString(" ").lowercase() }.firstOrNull()
            metroName?.let {
                metroStationRepository.findByTitleIgnoreCase(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getAvailableMetroStations(): List<MetroStation> {
        return metroStationRepository.findAll().toList().sortedBy { it.title }
    }
}