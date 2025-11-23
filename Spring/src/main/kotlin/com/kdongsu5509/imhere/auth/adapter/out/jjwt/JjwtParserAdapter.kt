package com.kdongsu5509.imhere.auth.adapter.out.jjwt

import com.kdongsu5509.imhere.auth.adapter.out.dto.OIDCPublicKey
import com.kdongsu5509.imhere.auth.application.dto.OIDCDecodePayload
import com.kdongsu5509.imhere.auth.application.port.out.JwtParserPort
import com.kdongsu5509.imhere.auth.application.port.out.JwtVerficationPort
import com.kdongsu5509.imhere.auth.application.port.out.LoadPublicKeyPort
import com.kdongsu5509.imhere.common.exception.implementation.auth.OIDCInvalidException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Component

@Component
class JjwtParserAdapter(
    private val loadPublicKeyPort: LoadPublicKeyPort,
    private val kakaoOIDCProperties: KakaoOIDCProperties,
    private val jwtVerficationPort: JwtVerficationPort
) : JwtParserPort {
    override fun parse(idToken: String): OIDCDecodePayload {
        val oidcPublicKey: OIDCPublicKey = findProperOIDCPublicKey(idToken)

        val jws = jwtVerficationPort.verifySignature(
            idToken,
            oidcPublicKey.n,
            oidcPublicKey.e
        )

        return extractPayloadFromJws(jws)
    }

    private fun findProperOIDCPublicKey(idToken: String): OIDCPublicKey {
        val kidFromOriginTokenHeader = getKidFromOriginTokenHeader(idToken)
        return loadPublicKeyPort.loadPublicKey(kidFromOriginTokenHeader)
    }

    fun getKidFromOriginTokenHeader(token: String): String {
        val kakaoIss = kakaoOIDCProperties.issuer
        val kakaoAud = kakaoOIDCProperties.audience
        val parseClaimsJwt = Jwts.parserBuilder()
            .requireAudience(kakaoAud)
            .requireIssuer(kakaoIss)
            .build()
            .parseClaimsJwt(getUnsignedToken(token))
        return parseClaimsJwt.header["kid"] as String
    }

    private fun getUnsignedToken(token: String): String {
        val splitToken = token.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (splitToken.size != 3) {
            throw OIDCInvalidException(detailMessage = "토큰 형식이 올바르지 않습니다.")
        }
        return "${splitToken[0]}.${splitToken[1]}."
    }

    private fun extractPayloadFromJws(jws: Jws<Claims>): OIDCDecodePayload {
        val body = jws.body
        return OIDCDecodePayload(
            iss = body.issuer,
            aud = body.audience,
            sub = body.subject,
            email = body.get("email", String::class.java)
        )
    }
}