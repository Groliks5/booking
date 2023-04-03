package com.itpw.booking.user

import com.itpw.booking.exceptions.DetailException
import com.itpw.booking.exceptions.ForbiddenException
import com.itpw.booking.exceptions.NotFoundException
import com.itpw.booking.util.Translator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.logging.Logger
import kotlin.math.log

@Service
class UserService @Autowired constructor(
    private val userRepository: UserRepository,
    private val translator: Translator
) {
    private val passwordEncoder = BCryptPasswordEncoder()

    fun getUser(userId: Long): User {
        return userRepository.findByIdOrNull(userId) ?: throw NotFoundException(translator.toLocale("user_not_found"))
    }

    fun registerUser(request: RegisterUserRequest): User {
        if (userRepository.existsByLogin(request.login)) {
            throw DetailException(translator.toLocale("user_already_exists_error"))
        }
        val newUser = User(
            login = request.login,
            password = passwordEncoder.encode(request.password),
            phone = request.phone.filter { it.isDigit() }
        )
        return userRepository.save(newUser)
    }

    fun login(request: LoginUserRequest): User {
        val user = userRepository.findByLogin(request.login) ?: throw NotFoundException(translator.toLocale("user_not_found"))
        if (user.role != UserRole.USER && !user.isApproved) {
            throw ForbiddenException(translator.toLocale("access_denied"))
        }
        if (!passwordEncoder.matches(request.password, user.password)) {
            throw ForbiddenException(translator.toLocale("wrong_password_error"))
        }
        return user
    }

    fun isUserExists(login: String): Boolean {
        return userRepository.existsByLogin(login)
    }

    fun editUser(userId: Long, request: EditUserRequest): User {
        val user = getUser(userId)
        user.name = request.name
        user.email = request.email
        user.phone = request.phone.filter { it.isDigit() }
        user.whatsApp = request.whatsApp
        user.viber = request.viber
        user.telegram = request.telegram
        user.vk = request.vk
        return userRepository.save(user)
    }

    fun changePassword(userId: Long, request: ChangePasswordRequest): User {
        val user = getUser(userId)
        user.password = passwordEncoder.encode(request.newPassword)
        return userRepository.save(user)
    }
}