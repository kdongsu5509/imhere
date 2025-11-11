package com.kdongsu5509.imhere.auth.application.service.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

// 토큰 클레임(Claim)으로 생성되는 최소 UserDetails 구현체
class SimpleTokenUserDetails(
    private val email: String,
    role: String
) : UserDetails {

    // 토큰의 role 클레임을 Spring Security Authority 객체로 변환
    private val authoritiesList: Collection<GrantedAuthority> = listOf(SimpleGrantedAuthority(role))

    override fun getAuthorities(): Collection<GrantedAuthority> = authoritiesList

    // ID 토큰에서 sub 또는 email을 username으로 사용합니다.
    override fun getUsername(): String = email

    // Password는 사용하지 않으므로 null/빈 문자열 반환
    override fun getPassword(): String? = null

    // 이하 메서드는 모두 true를 반환하여 계정 만료/잠금 등을 '유효' 상태로 유지
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true
}