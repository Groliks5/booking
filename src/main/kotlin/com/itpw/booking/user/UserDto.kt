package com.itpw.booking.user

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UserResponse(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("email")
    val email: String?,
    @JsonProperty("phone")
    val phone: String,
    @JsonProperty("whats_app")
    val whatsApp: String?,
    @JsonProperty("viber")
    val viber: String?,
    @JsonProperty("telegram")
    val telegram: String?,
    @JsonProperty("vk")
    val vk: String?
) {
    constructor(user: User): this(
        id = user.id,
        name = user.name,
        email = user.email,
        phone = user.phone,
        whatsApp = user.whatsApp,
        viber = user.viber,
        telegram = user.telegram,
        vk = user.vk
    )
}

data class LoginResponse(
    @JsonProperty("token")
    val token: String,
    @JsonProperty("user")
    val user: UserResponse
)

data class RegisterUserRequest(
    @JsonProperty("login")
    @field:Size(min = 3, max = 30, message = "{login_length_error}")
    val login: String,
    @JsonProperty("password")
    @field:Size(min = 6, max = 30, message = "{password_length_error}")
    val password: String,
    @JsonProperty("phone")
    val phone: String
)

data class LoginUserRequest(
    @JsonProperty("login")
    @field:Size(min = 3, max = 30, message = "{login_length_error}")
    val login: String,
    @JsonProperty("password")
    @field:Size(min = 6, max = 30, message = "{password_length_error}")
    val password: String,
)

data class EditUserRequest(
    @JsonProperty("name")
    @field:Size(min = 0, max = 100, message = "{name_length_error}")
    val name: String,
    @JsonProperty("email")
    @field:Email(message = "{email_format_error}")
    val email: String?,
    @JsonProperty("phone")
    @field:Size(min = 11, message = "{phone_length_error}")
    val phone: String,
    @JsonProperty("whats_app")
    val whatsApp: String?,
    @JsonProperty("viber")
    val viber: String?,
    @JsonProperty("telegram")
    val telegram: String?,
    @JsonProperty("vk")
    val vk: String?
)

data class ChangePasswordRequest(
    @JsonProperty("new_password")
    @field:Size(min = 6, max = 30, message = "{password_length_error}")
    val newPassword: String
)

data class UserShortInfoResponse(
    @JsonProperty("name")
    val name: String,
    @JsonProperty("email")
    val email: String?,
    @JsonProperty("phone")
    val phone: String,
    @JsonProperty("whats_app")
    val whatsApp: String?,
    @JsonProperty("viber")
    val viber: String?,
    @JsonProperty("telegram")
    val telegram: String?,
    @JsonProperty("vk")
    val vk: String?
) {
    constructor(user: User): this(
        name = user.name,
        email = user.email,
        phone = user.phone,
        whatsApp = user.whatsApp,
        viber = user.viber,
        telegram = user.telegram,
        vk = user.vk
    )
}