package com.itpw.booking.metro_station

import com.fasterxml.jackson.annotation.JsonProperty

data class GeocoderResponse(
    @JsonProperty("response")
    val response: GeocoderResponseField
)

data class GeocoderResponseField(
    @JsonProperty("GeoObjectCollection")
    val geoObjectCollection: GeoObjectCollection
)

data class GeoObjectCollection(
    @JsonProperty("featureMember")
    val featureMember: List<FeatureMember>
)

data class FeatureMember(
    @JsonProperty("GeoObject")
    val geoObject: GeoObject
)

data class GeoObject(
    @JsonProperty("metaDataProperty")
    val metaDataProperty: MetaDataProperty
)

data class MetaDataProperty(
    @JsonProperty("GeocoderMetaData")
    val geocoderMetaData: GeocoderMetaData
)

data class GeocoderMetaData(
    @JsonProperty("Address")
    val address: Address
)

data class Address(
    @JsonProperty("Components")
    val components: List<Component>
)

data class Component(
    @JsonProperty("kind")
    val kind: String,
    @JsonProperty("name")
    val name: String
)