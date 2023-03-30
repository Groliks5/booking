package com.itpw.booking.user

import com.itpw.booking.notice.Notice
import jakarta.persistence.*
import java.util.Calendar

@Entity
@Table(
    name = "users"
)
class User(
    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY,
    )
    val id: Long = -1L,
    var name: String = "",
    var email: String? = null,
    var phone: String,
    val login: String,
    var password: String,
    var whatsApp: String? = null,
    var viber: String? = null,
    var telegram: String? = null,
    var vk: String? = null,
    @Enumerated(EnumType.STRING)
    val role: UserRole = UserRole.USER,
    var isApproved: Boolean = false,
    @Temporal(TemporalType.TIMESTAMP)
    val registerDate: Calendar = Calendar.getInstance(),
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "creator")
    val notices: MutableList<Notice> = mutableListOf()
)