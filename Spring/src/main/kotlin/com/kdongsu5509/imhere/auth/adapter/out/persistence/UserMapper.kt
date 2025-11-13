package com.kdongsu5509.imhere.auth.adapter.out.persistence

import com.kdongsu5509.imhere.auth.domain.User
import org.springframework.stereotype.Component

@Component
class UserMapper {
    fun mapToJpaEntity(domainUser: User): UserJpaEntity {
        return UserJpaEntity(
            domainUser.email,
            domainUser.role,
            domainUser.oauthProvider
        )
    }

    fun mapToDomainEntity(jpaEntity: UserJpaEntity): User {
        return User(
            jpaEntity.email,
            jpaEntity.provider,
            jpaEntity.role
        )
    }
}