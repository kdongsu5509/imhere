package com.kdongsu5509.imhere.auth.adapter.out.persistence

import com.kdongsu5509.imhere.auth.application.port.out.CheckUserPort
import com.kdongsu5509.imhere.auth.application.port.out.LoadUserPort
import com.kdongsu5509.imhere.auth.application.port.out.SaveUserPort
import com.kdongsu5509.imhere.auth.domain.User
import org.springframework.stereotype.Component


@Component
class UserPersistenceAdapter(
    private val springDataUserRepository: SpringDataUserRepository
) : CheckUserPort, SaveUserPort, LoadUserPort {
    override fun existsByEmail(email: String): Boolean {
        return springDataUserRepository.existsByEmail(email)
    }

    override fun save(user: User) {
        // 1. 도메인 -> 엔티티
        springDataUserRepository.save(user.toUserEntity())
    }

    override fun findByEmail(email: String): User? {
        val findByEmail: UserJpaEntity? = springDataUserRepository.findByEmail(email)
        if (findByEmail != null) {
            return findByEmail.toUser()
        }
        throw IllegalArgumentException("User $email not found")
    }
}