package com.itpw.booking.util

import com.itpw.booking.user.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.logging.Logger


@Component
class JwtAuthTokenFilter @Autowired constructor(
    private val tokenProvider: JwtSigner,
    private val userRepository: UserRepository
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val jwt = AuthenticationHeaderParser.getToken(request.getHeader("Authorization"))
            logger.error(jwt)
            if (!jwt.isNullOrBlank()) {
                val userId = tokenProvider.getJwtSubject(jwt).toLong()

                val userDetails = userRepository.findByIdOrNull(userId)?.let {
                    object : UserDetails {
                        override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
                            return mutableListOf(SimpleGrantedAuthority("user"))
                        }

                        override fun getPassword(): String {
                            return it.password
                        }

                        override fun getUsername(): String {
                            return it.id.toString()
                        }

                        override fun isAccountNonExpired(): Boolean {
                            return true
                        }

                        override fun isAccountNonLocked(): Boolean {
                            return true
                        }

                        override fun isCredentialsNonExpired(): Boolean {
                            return true
                        }

                        override fun isEnabled(): Boolean {
                            return true
                        }
                    }
                }
                if (userDetails != null) {
                    val authentication = UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.authorities)
                    authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

                    SecurityContextHolder.getContext().authentication = authentication
                }
            }
        } catch (e: Exception) {

        }
        filterChain.doFilter(request, response)
    }
}