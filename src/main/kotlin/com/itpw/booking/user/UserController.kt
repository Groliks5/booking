package com.itpw.booking.user

import com.itpw.booking.util.DetailsResponse
import com.itpw.booking.util.JwtSigner
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.logging.Logger

@RestController
@RequestMapping("/user")
class UserController @Autowired constructor(
    private val userService: UserService,
    private val jwtSigner: JwtSigner
) {
    @GetMapping("")
    fun getUser(
        authentication: Authentication
    ): UserResponse {
        val user = userService.getUser(authentication.name.toLong())
        return UserResponse(user)
    }

    @PostMapping("/register")
    fun registerUser(
        @Valid @RequestBody request: RegisterUserRequest
    ): LoginResponse {
        val user = userService.registerUser(request)
        val token = jwtSigner.createJwt(user.id.toString())
        return LoginResponse(token, UserResponse(user))
    }

    @PostMapping("/login")
    fun loginUser(
        @Valid @RequestBody request: LoginUserRequest
    ): LoginResponse {
        val user = userService.login(request)
        val token = jwtSigner.createJwt(user.id.toString())
        return LoginResponse(token, UserResponse(user))
    }

    @PutMapping("")
    fun editUser(
        authentication: Authentication,
        @Valid @RequestBody request: EditUserRequest
    ): UserResponse {
        val user = userService.editUser(authentication.name.toLong(), request)
        return UserResponse(user)
    }

    @PutMapping("/change_password")
    fun changePassword(
        authentication: Authentication,
        @Valid @RequestBody request: ChangePasswordRequest
    ): UserResponse {
        val user = userService.changePassword(authentication.name.toLong(), request)
        return UserResponse(user)
    }

    @DeleteMapping("")
    fun deleteUser(
        authentication: Authentication
    ): DetailsResponse {
        userService.deleteUser(authentication.name.toLong())
        return DetailsResponse("Пользователь успешно удалён")
    }
}