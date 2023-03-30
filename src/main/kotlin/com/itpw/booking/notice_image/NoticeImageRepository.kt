package com.itpw.booking.notice_image

import org.springframework.data.repository.CrudRepository

interface NoticeImageRepository: CrudRepository<NoticeImage, Long> {
    fun findByHref(href: String): NoticeImage?
    fun deleteByHref(href: String)
}