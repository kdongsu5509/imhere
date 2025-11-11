//package com.kdongsu5509.imhere.common.config
//
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.security.config.annotation.web.builders.HttpSecurity
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
//import org.springframework.security.config.annotation.web.invoke
//import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter
//import org.springframework.security.web.SecurityFilterChain
//
//@Configuration
//@EnableWebSecurity
//class SecurityConfig {
//
//    var temp: OAuth2AuthorizationRequestRedirectFilter
//
//    @Bean
//    fun filterChain(http: HttpSecurity): SecurityFilterChain {
//        http {
//            // 1. 보안 기능 비활성화 및 설정
//            // REST API 서버의 경우 보통 CSRF를 비활성화합니다.
//            csrf { disable() }
//
//            // 기본 폼 로그인 비활성화
//            formLogin { disable() }
//
//            // HTTP Basic 인증 비활성화
//            httpBasic { disable() }
//
//            // 2. OAuth2 로그인 활성화
//            // 소셜 로그인 등을 위해 OAuth 2.0 기능을 활성화합니다.
//            oauth2Login {}
//
//            // 3. 인가 설정 (모든 요청 허용)
//            authorizeHttpRequests {
//                // 어떤 요청이든 인증 없이 허용 (개발 초기 단계에 유용)
//                authorize(anyRequest, permitAll)
//
//                // 만약 특정 경로만 인증이 필요하다면 아래처럼 변경 가능:
//                // authorize("/api/**", authenticated) // /api로 시작하는 요청은 인증 필수
//                // authorize(antMatcher("/public/**"), permitAll) // /public으로 시작하는 요청은 허용
//            }
//        }
//        // 설정된 HttpSecurity를 기반으로 SecurityFilterChain 빌드 및 반환
//        return http.build()
//    }
//}