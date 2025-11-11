package com.kdongsu5509.imhere.auth.application.service

import com.kdongsu5509.imhere.auth.application.port.out.LoadUserPort
import com.kdongsu5509.imhere.auth.application.port.out.SaveUserPort
import com.kdongsu5509.imhere.auth.domain.OAuth2Provider
import com.kdongsu5509.imhere.auth.domain.User
import com.kdongsu5509.imhere.auth.domain.UserRole
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service // 또는 @Service
class AuthTransactionHandler(
    private val saveUserPort: SaveUserPort,
    private val loadUserPort: LoadUserPort
) {
    // 1. 사용자 저장 트랜잭션
    @Transactional
    fun saveUser(email: String, provider: OAuth2Provider) {
        print("[TRANSACTION] saveUser 시작")
        val user = User(email, provider, UserRole.NORMAL)
        print("USER 객체 생성")
        saveUserPort.save(user)
    }

    @Transactional(readOnly = true)
    fun loadUser(email: String): User? {
        return loadUserPort.findByEmail(email)
    }
}