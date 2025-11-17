package com.kdongsu5509.imhere.auth.application.service

import com.kdongsu5509.imhere.auth.application.service.jwt.JwtTokenUtil
import com.kdongsu5509.imhere.auth.application.service.security.SimpleTokenUserDetails
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenUtil: JwtTokenUtil
) : OncePerRequestFilter() {

    companion object {
        private const val BEARER_PREFIX = "Bearer "
        private const val AUTH_HEADER = "Authorization"
    }

    public override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        request.servletPath.startsWith("/api/v1/auth")
        return request.servletPath.startsWith("/actuator")
    }

    public override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val jwt = getJwtFromRequestHeader(request)

        if (jwt != null) {
            // 2. 토큰 유효성 검증
            if (jwtTokenUtil.validateToken(jwt)) {

                // 4. 인증 처리: DB 조회 없이 토큰 정보로 UserDetails 생성
                val email = jwtTokenUtil.getUsernameFromToken(jwt)
                val role = jwtTokenUtil.getRoleFromToken(jwt)

                // 이메일(사용자 고유 식별자)이 유효하고, SecurityContext에 인증 정보가 없는 경우에만 진행
                if (SecurityContextHolder.getContext().authentication == null) {
                    val userDetails: UserDetails = SimpleTokenUserDetails(email, role)

                    val authentication = UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.authorities
                    )

                    // 인증 세부 정보 설정
                    authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

                    // SecurityContext에 인증 객체 저장
                    SecurityContextHolder.getContext().authentication = authentication
                }
            } else {
                // 토큰이 유효하지 않은 경우 401 응답 후 중단
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT token.")
                return
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun getJwtFromRequestHeader(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(AUTH_HEADER)

        return if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            bearerToken.substring(BEARER_PREFIX.length)
        } else {
            null
        }
    }

    private fun sendErrorResponse(response: HttpServletResponse, status: Int, message: String) {
        runCatching {
            response.status = status
            response.contentType = "application/json;charset=UTF-8"

            val out = response.writer
            out.print("""{"error": "$message"}""")
            out.flush()
        }.onFailure { e ->
            logger.error("Error writing error response", e)
        }
    }
}