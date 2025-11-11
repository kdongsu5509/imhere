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
// 💡 CustomUserDetailService 의존성 제거
class JwtAuthenticationFilter(
    private val jwtTokenUtil: JwtTokenUtil
    // 💡 (이전에 있던 RedisTokenService도 자체 토큰 검증 로직에서 필요하지 않다면 제거 가능)
) : OncePerRequestFilter() {

    companion object {
        private const val BEARER_PREFIX = "Bearer "
        private const val AUTH_HEADER = "Authorization"
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return request.servletPath.startsWith("/actuator")
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val jwt = getJwtFromRequest(request)

        if (jwt != null) {
            // 2. 토큰 유효성 검증
            if (jwtTokenUtil.validateToken(jwt)) {

                // 4. 인증 처리: DB 조회 없이 토큰 정보로 UserDetails 생성
                val email = jwtTokenUtil.getUsernameFromToken(jwt) // 💡 이메일 클레임 추출 가정
                val role = jwtTokenUtil.getRoleFromToken(jwt)   // 💡 역할 클레임 추출 가정

                // 이메일(사용자 고유 식별자)이 유효하고, SecurityContext에 인증 정보가 없는 경우에만 진행
                if (email != null && role != null && SecurityContextHolder.getContext().authentication == null) {

                    // 💡 SimpleTokenUserDetails를 사용하여 UserDetails 객체 즉시 생성
                    val userDetails: UserDetails = SimpleTokenUserDetails(email, role)

                    val authentication = UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // 비밀번호는 Oauth 인증에서 사용하지 않으므로 null
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

    /**
     * HTTP 요청 헤더에서 JWT 토큰을 추출합니다.
     */
    private fun getJwtFromRequest(request: HttpServletRequest): String? {
        // Authorization 헤더 값을 가져옵니다.
        val bearerToken = request.getHeader(AUTH_HEADER)

        // "Bearer "로 시작하는지 확인하고 접두사를 제거하여 토큰만 반환합니다.
        return if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            bearerToken.substring(BEARER_PREFIX.length)
        } else {
            null
        }
    }

    /**
     * 오류 응답을 JSON 형식으로 작성하여 클라이언트에 보냅니다.
     */
    private fun sendErrorResponse(response: HttpServletResponse, status: Int, message: String) {
        // 💡 runCatching을 사용하여 IOException 발생 가능성을 명시적으로 처리합니다.
        runCatching {
            response.status = status
            response.contentType = "application/json;charset=UTF-8"

            val out = response.writer
            // Kotlin의 템플릿 문자열을 사용하여 JSON 포맷팅
            out.print("""{"error": "$message"}""")
            out.flush()
        }.onFailure { e ->
            logger.error("Error writing error response", e)
        }
    }
}