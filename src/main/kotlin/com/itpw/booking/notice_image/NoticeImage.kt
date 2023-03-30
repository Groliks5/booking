package com.itpw.booking.notice_image

import com.itpw.booking.notice.Notice
import jakarta.persistence.*

@Entity
@Table(
    name = "notice_images"
)
class NoticeImage (
    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY,
    )
    val id: Long = -1L,
    var href: String,
    @ManyToOne
    val notice: Notice,
    var position: Int
)