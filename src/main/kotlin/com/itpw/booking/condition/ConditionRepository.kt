package com.itpw.booking.condition

import org.aspectj.apache.bcel.classfile.Code
import org.springframework.data.repository.CrudRepository

interface ConditionRepository: CrudRepository<Condition, Long> {
    fun findByTitleIgnoreCase(title: String): Condition?
    fun findByIdIn(ids: List<Long>): List<Condition>
}