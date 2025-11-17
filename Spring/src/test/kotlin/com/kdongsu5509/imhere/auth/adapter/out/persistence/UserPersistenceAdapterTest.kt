package com.kdongsu5509.imhere.auth.adapter.out.persistence

import com.kdongsu5509.imhere.auth.domain.OAuth2Provider
import com.kdongsu5509.imhere.auth.domain.User
import com.kdongsu5509.imhere.auth.domain.UserRole
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class UserPersistenceAdapterTest {
    @Mock
    lateinit var userMapper: UserMapper

    @Mock
    lateinit var springDataUserRepository: SpringDataUserRepository

    @InjectMocks
    lateinit var userPersistenceAdapter: UserPersistenceAdapter

    companion object {
        val testEmail = "test@test.com"
        val testUser = User(testEmail, OAuth2Provider.KAKAO, UserRole.NORMAL)
        val testUserEntity = UserJpaEntity(testUser.email, testUser.role, OAuth2Provider.KAKAO)
    }

    @Test
    @DisplayName("존재하는 이메일은 true을 반환한다")
    fun existByEmail_success() {
        //given
        val email = "test@test.com"
        `when`(springDataUserRepository.existsByEmail(email)).thenReturn(true);

        //when
        val result = userPersistenceAdapter.existsByEmail(email)

        //then
        Assertions.assertThat(result).isTrue
    }

    @Test
    @DisplayName("존재하지 않는 이메일이라고 확인되면 false를 반환한다")
    fun existByEmail_fail() {
        //given
        `when`(springDataUserRepository.existsByEmail(testEmail)).thenReturn(false)

        //when
        val result = userPersistenceAdapter.existsByEmail(testEmail)

        //then
        Assertions.assertThat(result).isFalse
    }

    @Test
    @DisplayName("사용자를 잘 저장한다")
    fun saveUser() {
        //given
        `when`(userMapper.mapToJpaEntity(testUser)).thenReturn(
            testUserEntity
        )
        given(springDataUserRepository.save(testUserEntity))
            .willReturn(testUserEntity)

        //when
        userPersistenceAdapter.save(testUser)

        //then
        verify(springDataUserRepository).save(testUserEntity)
    }

    @Test
    @DisplayName("사용자가 존재하면 잘 찾아온다")
    fun findByEmail_success() {
        //given
        `when`(springDataUserRepository.findByEmail(testEmail)).thenReturn(testUserEntity)
        `when`(userMapper.mapToDomainEntity(testUserEntity)).thenReturn(testUser)

        //when
        val result = userPersistenceAdapter.findByEmail(testEmail)

        //then
        org.junit.jupiter.api.Assertions.assertEquals(testEmail, result.email)

        verify(springDataUserRepository).findByEmail(testEmail)
    }
}