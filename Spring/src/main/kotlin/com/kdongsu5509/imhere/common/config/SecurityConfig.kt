package com.kdongsu5509.imhere.common.config

import com.kdongsu5509.imhere.auth.application.service.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            // 1. 보안 기능 비활성화 및 설정
            csrf { disable() }           // REST API 서버이므로 CSRF 비활성화
            formLogin { disable() }      // 기본 폼 로그인 비활성화
            httpBasic { disable() }      // HTTP Basic 인증 비활성화

            // 💡 OAuth2 Login 기능을 비활성화합니다. (프론트 주도 인증)
            // oauth2Login {} // 제거

            // JWT를 사용하므로 세션을 사용하지 않습니다.
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }

            // 2. JWT 필터 적용: 기본 인증 필터가 동작하기 전에 실행되도록 등록
            // UsernamePasswordAuthenticationFilter 이전에 필터를 등록합니다.
            addFilterBefore<UsernamePasswordAuthenticationFilter>(jwtAuthenticationFilter)

            // 3. 인가 설정
            authorizeHttpRequests {
                // 토큰 검증 API (프론트에서 ID 토큰 또는 리프레시 토큰을 보내는 경로)는 모두 허용
                authorize("/api/*/auth", permitAll)

                // Actuator 경로도 허용
                authorize("/actuator/**", permitAll)

                // 그 외 모든 요청은 인증된 사용자(JWT 검증 통과)에게만 허용
                authorize(anyRequest, authenticated)
            }
        }
        // 설정된 HttpSecurity를 기반으로 SecurityFilterChain 빌드 및 반환
        return http.build()
    }
}