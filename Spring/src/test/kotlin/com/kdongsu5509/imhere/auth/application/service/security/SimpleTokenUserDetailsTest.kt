package com.kdongsu5509.imhere.auth.application.service.security

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.security.core.authority.SimpleGrantedAuthority

class SimpleTokenUserDetailsTest {

    @Test
    @DisplayName("이메일과 역할로 UserDetails를 생성한다")
    fun createUserDetails_success() {
        // given
        val email = "test@example.com"
        val role = "ROLE_USER"

        // when
        val userDetails = SimpleTokenUserDetails(email, role)

        // then
        assertThat(userDetails.username).isEqualTo(email)
        assertThat(userDetails.password).isNull()
        assertThat(userDetails.authorities).hasSize(1)
        assertThat(userDetails.authorities.first()).isEqualTo(SimpleGrantedAuthority(role))
    }

    @Test
    @DisplayName("계정이 만료되지 않았다고 반환한다")
    fun isAccountNonExpired_returnsTrue() {
        // given
        val userDetails = SimpleTokenUserDetails("test@example.com", "ROLE_USER")

        // when
        val result = userDetails.isAccountNonExpired

        // then
        assertThat(result).isTrue()
    }

    @Test
    @DisplayName("계정이 잠기지 않았다고 반환한다")
    fun isAccountNonLocked_returnsTrue() {
        // given
        val userDetails = SimpleTokenUserDetails("test@example.com", "ROLE_USER")

        // when
        val result = userDetails.isAccountNonLocked

        // then
        assertThat(result).isTrue()
    }

    @Test
    @DisplayName("자격 증명이 만료되지 않았다고 반환한다")
    fun isCredentialsNonExpired_returnsTrue() {
        // given
        val userDetails = SimpleTokenUserDetails("test@example.com", "ROLE_USER")

        // when
        val result = userDetails.isCredentialsNonExpired

        // then
        assertThat(result).isTrue()
    }

    @Test
    @DisplayName("계정이 활성화되어 있다고 반환한다")
    fun isEnabled_returnsTrue() {
        // given
        val userDetails = SimpleTokenUserDetails("test@example.com", "ROLE_USER")

        // when
        val result = userDetails.isEnabled

        // then
        assertThat(result).isTrue()
    }

    @Test
    @DisplayName("다양한 역할로 UserDetails를 생성할 수 있다")
    fun createUserDetailsWithDifferentRoles() {
        // given & when & then
        val adminDetails = SimpleTokenUserDetails("admin@example.com", "ROLE_ADMIN")
        assertThat(adminDetails.authorities.first().authority).isEqualTo("ROLE_ADMIN")

        val userDetails = SimpleTokenUserDetails("user@example.com", "ROLE_USER")
        assertThat(userDetails.authorities.first().authority).isEqualTo("ROLE_USER")
    }
}

