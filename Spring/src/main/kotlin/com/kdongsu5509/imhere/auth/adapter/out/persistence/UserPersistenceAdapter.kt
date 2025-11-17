package com.kdongsu5509.imhere.auth.adapter.out.persistence

import com.kdongsu5509.imhere.auth.application.port.out.CheckUserPort
import com.kdongsu5509.imhere.auth.application.port.out.LoadUserPort
import com.kdongsu5509.imhere.auth.application.port.out.SaveUserPort
import com.kdongsu5509.imhere.auth.domain.User
import com.kdongsu5509.imhere.common.exception.implementation.auth.UserNotFoundException
import org.springframework.stereotype.Component

@Component
class UserPersistenceAdapter(
    private val userMapper: UserMapper,
    private val springDataUserRepository: SpringDataUserRepository
) : CheckUserPort, SaveUserPort, LoadUserPort {
    override fun existsByEmail(email: String): Boolean {
        return springDataUserRepository.existsByEmail(email)
    }

    override fun save(user: User) {
        val jpaEntity = userMapper.mapToJpaEntity(user)
        springDataUserRepository.save(jpaEntity)
    }

    override fun findByEmail(email: String): User {
        val findJpaEntity: UserJpaEntity? = springDataUserRepository.findByEmail(email)
        findJpaEntity ?: throw UserNotFoundException()
        return userMapper.mapToDomainEntity(findJpaEntity)
    }
}