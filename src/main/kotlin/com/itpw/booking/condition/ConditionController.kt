package com.itpw.booking.condition

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/condition")
class ConditionController @Autowired constructor(
    private val conditionService: ConditionService
) {
    @GetMapping("")
    fun getConditions(): List<ConditionResponse> {
        val conditions = conditionService.getConditions()
        return conditions.map { ConditionResponse(it) }
    }
}