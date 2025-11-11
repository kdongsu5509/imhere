package com.kdongsu5509.imhere.auth.application.service

import com.kdongsu5509.imhere.auth.application.dto.SelfSignedJWT
import com.kdongsu5509.imhere.auth.application.port.out.CheckUserPort
import com.kdongsu5509.imhere.auth.application.port.out.LoadUserPort
import com.kdongsu5509.imhere.auth.application.port.out.SaveUserPort
import com.kdongsu5509.imhere.auth.application.service.jwt.JwtTokenProvider
import com.kdongsu5509.imhere.auth.application.service.oidc.KakaoOIDCVerifier
import com.kdongsu5509.imhere.auth.domain.OAuth2Provider
import org.springframework.stereotype.Service

@Service
class OIDCHandlerImpl(
    private val kakaoOIDCVerifier: KakaoOIDCVerifier,
    private val checkUserPort: CheckUserPort,
    private val saveUserPort: SaveUserPort,
    private val jwtTokenProvider: JwtTokenProvider,
    private val loadUserPort: LoadUserPort,
    private val authTransactionHandler: AuthTransactionHandler
) : OIDCHandler() {
    override fun verifyIdTokenAndReturnJwt(idToken: String, provider: OAuth2Provider): SelfSignedJWT {
        val email = verifyOIDC(idToken)
        return issueJwt(email, provider)
    }

    override fun verifyOIDC(oidc: String): String {
        return kakaoOIDCVerifier.verifyAndReturnEmail(oidc)
    }

    override fun issueJwt(email: String, provider: OAuth2Provider): SelfSignedJWT {
        if (!checkUserPort.existsByEmail(email)) {
            print("저저저저ㅓ저ㅓ저ㅓ저저ㅓ장하는 로직을 시작함")
            authTransactionHandler.saveUser(email, provider)
        }
        print("사용자 존재하는지 확인하는 과정 끝 || 그리고 저장하는 것도 확인 완료")

        val savedUser = authTransactionHandler.loadUser(email)

        print("저장된 사용자를 가져오는 로직 완료")
        // 3. JWT 발급
        return jwtTokenProvider.issueJwtAuth(
            savedUser!!.email,
            savedUser.role.toString()
        )
    }
}