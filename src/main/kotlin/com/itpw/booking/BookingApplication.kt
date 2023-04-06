package com.itpw.booking

import com.itpw.booking.exceptions.DetailException
import com.itpw.booking.exceptions.ForbiddenException
import com.itpw.booking.exceptions.NotFoundException
import com.itpw.booking.properties.FileStorageProperties
import com.itpw.booking.properties.ProxyProperties
import com.itpw.booking.properties.YandexProperties
import com.itpw.booking.util.DetailsResponse
import com.itpw.booking.util.JwtAuthTokenFilter
import jakarta.validation.ConstraintViolationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.http.ResponseEntity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver
import java.util.*

@SpringBootApplication
class BookingApplication

fun main(args: Array<String>) {
	runApplication<BookingApplication>(*args)
}


@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(FileStorageProperties::class, ProxyProperties::class, YandexProperties::class)
class WebConfig @Autowired constructor(
	private val filter: JwtAuthTokenFilter,
) {
	@Bean
	@Throws(java.lang.Exception::class)
	fun configure(http: HttpSecurity): SecurityFilterChain? {
		http
			.cors{  }.csrf().disable()
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.authorizeHttpRequests()
			.requestMatchers("/media/get/**", "/user/register", "/user/login", "/user/reset_password", "/condition", "/additional_feature", "/notice/view/**", "/metro_station")
			.permitAll()
			.anyRequest()
			.authenticated()
		http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter::class.java)
		return http.build()
	}

	@Bean
	fun corsConfigurationSource(): CorsConfigurationSource {
		val configuration = CorsConfiguration()
		configuration.allowedOriginPatterns = listOf("*")
		configuration.setMaxAge(3600L)
		configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE")
		configuration.addAllowedHeader("*")
		val source = UrlBasedCorsConfigurationSource()
		source.registerCorsConfiguration("/**", configuration)
		return source
	}
	@Bean
	fun messageSource(): ResourceBundleMessageSource {
		val rs = ResourceBundleMessageSource()
		rs.setBasenames("messages")
		rs.setDefaultEncoding("UTF-8")
		rs.setUseCodeAsDefaultMessage(true)
		return rs
	}

	@Bean
	fun validator(messageSource: ResourceBundleMessageSource): LocalValidatorFactoryBean {
		val validator = LocalValidatorFactoryBean()
		validator.setValidationMessageSource(messageSource)
		return validator
	}

	@Bean
	fun localeResolver(): LocaleResolver {
		val resolver = AcceptHeaderLocaleResolver()
		resolver.supportedLocales = listOf(
			Locale("ru")
		)
		resolver.setDefaultLocale(
			Locale("ru")
		)
		return resolver
	}
}

@ControllerAdvice
class GlobalExceptionHandler {
	@ExceptionHandler(Exception::class)
	fun handleException(e: Exception): ResponseEntity<DetailsResponse> {
		e.printStackTrace()
		return ResponseEntity.badRequest().body(DetailsResponse("Произошла ошибка"))
	}

	@ExceptionHandler(ConstraintViolationException::class)
	fun handleConstraintViolationException(e: ConstraintViolationException): ResponseEntity<DetailsResponse> {
		return ResponseEntity.badRequest().body(DetailsResponse(e.constraintViolations.first().message))
	}

	@ExceptionHandler(MethodArgumentNotValidException::class)
	fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<DetailsResponse> {
		return ResponseEntity.badRequest().body(DetailsResponse(e.bindingResult.fieldErrors?.first()?.defaultMessage ?: ""))
	}

	@ExceptionHandler(DetailException::class)
	fun handleDetailException(e: DetailException): ResponseEntity<DetailsResponse> {
		return ResponseEntity.badRequest().body(DetailsResponse(e.detail))
	}

	@ExceptionHandler(NotFoundException::class)
	fun handleNotFoundException(e: NotFoundException): ResponseEntity<DetailsResponse> {
		return ResponseEntity.status(404).body(DetailsResponse(e.detail))
	}

	@ExceptionHandler(ForbiddenException::class)
	fun handleNotFoundException(e: ForbiddenException): ResponseEntity<DetailsResponse> {
		return ResponseEntity.status(403).body(DetailsResponse(e.detail))
	}
}