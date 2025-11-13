package com.kdongsu5509.imhere.auth.adapter.out.persistence

import com.kdongsu5509.imhere.auth.domain.OAuth2Provider
import com.kdongsu5509.imhere.auth.domain.User
import com.kdongsu5509.imhere.auth.domain.UserRole
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UserMapperTest {

    private var userMapper = UserMapper()

    @Test
    fun mapToJpaEntity() {
        //given
        val testUser = User(
            "temp@temp.com",
            OAuth2Provider.KAKAO,
            UserRole.NORMAL
        )

        // when
        val testJpaEntity = userMapper.mapToJpaEntity(testUser)

        // then
        assertThat(testJpaEntity)
            .isNotNull
            .extracting("email")
            .isEqualTo(testUser.email)
    }

    @Test
    fun mapToDomainEntity() {
        //given
        val testJpa = UserJpaEntity(
            "temp@temp.com",
            UserRole.NORMAL,
            OAuth2Provider.KAKAO
        )

        // when
        val user = userMapper.mapToDomainEntity(testJpa)

        // then
        assertThat(user)
            .isNotNull
            .extracting("email")
            .isEqualTo(testJpa.email)
    }
}