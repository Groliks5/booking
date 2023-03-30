package com.itpw.booking.user

import org.springframework.data.repository.CrudRepository

interface UserRepository: CrudRepository<User, Long> {
    fun existsByLogin(login: String): Boolean
    fun findByLogin(login: String): User?
}