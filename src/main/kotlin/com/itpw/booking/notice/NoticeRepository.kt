package com.itpw.booking.notice

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository

interface NoticeRepository : CrudRepository<Notice, Long>, CustomNoticeRepository {
    fun findByCreator_Id(userId: Long, pageable: Pageable): Page<Notice>
    fun findByIdIn(ids: List<Long>): List<Notice>
    fun findByMetroIsNull(): List<Notice>
}