package com.itpw.booking.condition

import com.itpw.booking.additional_feature.AdditionalFeature
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ConditionService @Autowired constructor(
    private val conditionRepository: ConditionRepository
) {
    fun getConditions(): List<Condition> {
        return conditionRepository.findAll().toList()
    }

    fun getOrCreateCondition(title: String): Condition {
        val condition = conditionRepository.findByTitleIgnoreCase(title)
        if (condition != null) {
            return condition
        } else {
            return conditionRepository.save(Condition(title = title))
        }
    }
}