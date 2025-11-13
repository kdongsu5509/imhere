package com.kdongsu5509.imhere.auth.application.service.oidc

import com.kdongsu5509.imhere.auth.application.dto.SelfSignedJWT
import com.kdongsu5509.imhere.auth.application.dto.UserInformation
import com.kdongsu5509.imhere.auth.application.port.`in`.HandleOIDCUseCase
import com.kdongsu5509.imhere.auth.application.port.`in`.IssueJWTPort
import com.kdongsu5509.imhere.auth.application.port.`in`.VerifyOIDCPort
import com.kdongsu5509.imhere.auth.application.port.out.CheckUserPort
import com.kdongsu5509.imhere.auth.application.port.out.LoadUserPort
import com.kdongsu5509.imhere.auth.application.port.out.OIDCVerificationPort
import com.kdongsu5509.imhere.auth.application.port.out.SaveUserPort
import com.kdongsu5509.imhere.auth.application.service.jwt.JwtTokenProvider
import com.kdongsu5509.imhere.auth.domain.OAuth2Provider
import com.kdongsu5509.imhere.auth.domain.User
import com.kdongsu5509.imhere.auth.domain.UserRole
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class OIDCLoginFacade(
    private val oidcVerificationPort: OIDCVerificationPort,
    private val checkUserPort: CheckUserPort,
    private val jwtTokenProvider: JwtTokenProvider,
    private val saveUserPort: SaveUserPort,
    private val loadUserPort: LoadUserPort,
) : HandleOIDCUseCase, VerifyOIDCPort, IssueJWTPort {
    override fun verifyIdTokenAndReturnJwt(idToken: String, oAuth2Provider: OAuth2Provider): SelfSignedJWT {
        val userInformation = verify(idToken, oAuth2Provider)
        return issue(userInformation.email, oAuth2Provider)
    }

    override fun verify(oidc: String, oAuth2Provider: OAuth2Provider): UserInformation {
        return oidcVerificationPort.verifyAndReturnUserInformation(oidc)
    }

    override fun issue(email: String, oauth2Provider: OAuth2Provider): SelfSignedJWT {
        if (!checkUserPort.existsByEmail(email)) {
            val user = User(email, oauth2Provider, UserRole.NORMAL)
            saveUserPort.save(user)
        }
        val savedUser = loadUserPort.findByEmail(email)

        return jwtTokenProvider.issueJwtAuth(
            savedUser!!.email,
            savedUser.role.toString()
        )
    }
}