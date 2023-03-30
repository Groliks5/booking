package com.itpw.booking.notice

import com.itpw.booking.additional_feature.AdditionalFeature
import com.itpw.booking.condition.Condition
import com.itpw.booking.metro_station.MetroStation
import com.itpw.booking.notice_image.NoticeImage
import com.itpw.booking.user.User
import jakarta.persistence.*

@Entity
@Table(
    name = "notices"
)
class Notice (
    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY,
    )
    val id: Long = -1L,
    var title: String,
    var address: String,
    var roomsCount: RoomsCount,
    var pricePerDay: Double,
    var pricePerHour: Double?,
    var pricePerNight: Double?,
    var floor: Int?,
    var maxFloorInEntrance: Int?,
    var square: Double?,
    @Column(length = 10_000)
    var extraInfo: String?,
    var longitude: Double,
    var latitude: Double,
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "notice")
    var images: MutableList<NoticeImage> = mutableListOf(),
    var deposit: ConditionType?,
    var prePayment: ConditionType?,
    @ManyToOne
    var metro: MetroStation?,
    @ManyToMany
    @JoinTable(
        name = "selected_conditions",
        joinColumns = [JoinColumn(name = "notice_id")],
        inverseJoinColumns = [JoinColumn(name = "condition_id")]
    )
    var selectedConditions: MutableList<Condition> = mutableListOf(),
    @ManyToMany
    @JoinTable(
        name = "selected_additional_features",
        joinColumns = [JoinColumn(name = "notice_id")],
        inverseJoinColumns = [JoinColumn(name = "additional_feature_id")]
    )
    var selectedAdditionalFeatures: MutableList<AdditionalFeature> = mutableListOf(),
    @ManyToOne
    val creator: User
) {
    fun getPriceForPeriod(period: NoticePeriod): Double {
        return when(period) {
            NoticePeriod.DAY -> pricePerDay
            NoticePeriod.HOUR -> pricePerHour ?: 0.0
            NoticePeriod.NIGHT -> pricePerNight ?: 0.0
        }
    }
}