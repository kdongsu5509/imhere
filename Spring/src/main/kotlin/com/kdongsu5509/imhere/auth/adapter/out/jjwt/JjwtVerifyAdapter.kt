package com.kdongsu5509.imhere.auth.adapter.out.jjwt

import com.kdongsu5509.imhere.auth.application.dto.OIDCDecodePayload
import com.kdongsu5509.imhere.auth.application.port.out.JwtVerficationPort
import com.kdongsu5509.imhere.common.exception.implementation.auth.*
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Component
import java.math.BigInteger
import java.security.Key
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import java.security.spec.RSAPublicKeySpec
import java.util.*

@Component
class JjwtVerifyAdapter(
    private val kakaoOIDCProperties: KakaoOIDCProperties,
) : JwtVerficationPort {

    override fun verifyPayLoad(payload: OIDCDecodePayload) {
        verifyIssuer(payload.iss)
        verifyAudience(payload.aud)
    }

    override fun verifySignature(
        token: String,
        modulus: String,
        exponent: String
    ): Jws<Claims> {
        return try {
            val publicKey = createRSAPublicKey(modulus, exponent)
            Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
        } catch (e: ExpiredJwtException) {
            throw OIDCExpiredException()
        }
    }

    private fun verifyIssuer(actualIssuer: String) {
        if (actualIssuer != kakaoOIDCProperties.issuer) {
            throw OIDCInvalidException(
                detailMessage = "토큰의 issuer가 일치하지 않습니다. 토큰 Issuer: $actualIssuer"
            )
        }
    }

    private fun verifyAudience(actualAudience: String) {
        if (actualAudience != kakaoOIDCProperties.audience) {
            throw OIDCInvalidException(
                detailMessage = "토큰의 audience가 일치하지 않습니다. 토큰 Audience: $actualAudience"
            )
        }
    }

    private fun createRSAPublicKey(modulus: String, exponent: String): Key {
        try {
            return createKey(modulus, exponent)
        } catch (e: NoSuchAlgorithmException) {
            throw AlgorithmNotFoundException()
        } catch (e: InvalidKeySpecException) {
            throw InvalidKeyException()
        } catch (e: IllegalArgumentException) {
            throw InvalidEncodingException()
        }
    }

    private fun createKey(modulus: String, exponent: String): Key {
        val keyFactory = KeyFactory.getInstance("RSA")
        val decodeN = Base64.getUrlDecoder().decode(modulus)
        val decodeE = Base64.getUrlDecoder().decode(exponent)
        val n = BigInteger(1, decodeN)
        val e = BigInteger(1, decodeE)

        val keySpec = RSAPublicKeySpec(n, e)
        return keyFactory.generatePublic(keySpec)
    }
}