package com.itpw.booking.condition

import org.springframework.data.repository.CrudRepository

interface ConditionRepository: CrudRepository<Condition, Long> {
    fun findByTitleIgnoreCase(title: String): Condition?
}