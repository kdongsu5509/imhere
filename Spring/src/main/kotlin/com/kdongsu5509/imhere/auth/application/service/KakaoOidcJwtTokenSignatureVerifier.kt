package com.kdongsu5509.imhere.auth.application.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import org.apache.commons.logging.LogFactory
import org.springframework.stereotype.Component
import java.math.BigInteger
import java.security.Key
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import java.security.spec.RSAPublicKeySpec
import java.util.*

@Component
class KakaoOidcJwtTokenSignatureVerifier {
    private val log = LogFactory.getLog(KakaoOidcJwtTokenSignatureVerifier::class.java)

    /**
     * 공개 키를 사용하여 ID 토큰의 서명 검증 및 JWS(JSON Web Signature) 객체 반환
     *
     * @param token 카카오 OIDC ID 토큰
     * @param modulus RSA 공개키의 modulus (Base64 URL-safe 인코딩)
     * @param exponent RSA 공개키의 exponent (Base64 URL-safe 인코딩)
     * @return 검증된 JWS 객체
     * @throws SecurityException 서명 검증 실패 또는 토큰 만료 시
     */
    fun verifyTokenSignature(
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
            throw SecurityException("만료된 토큰입니다.", e)
        } catch (e: Exception) {
            log.error("토큰 서명 검증 실패: ${e.message}", e)
            throw SecurityException("토큰 서명 검증에 실패했습니다. (${e.message})", e)
        }
    }

    /**
     * modulus와 exponent 값을 사용하여 RSA 공개 키(Key) 객체 생성
     *
     * @param modulus RSA 공개키의 modulus (Base64 URL-safe 인코딩)
     * @param exponent RSA 공개키의 exponent (Base64 URL-safe 인코딩)
     * @return RSA 공개키 객체
     * @throws SecurityException 키 생성 실패 시
     */
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    private fun createRSAPublicKey(modulus: String, exponent: String): Key {
        return try {
            val keyFactory = KeyFactory.getInstance("RSA")
            val decodeN = Base64.getUrlDecoder().decode(modulus)
            val decodeE = Base64.getUrlDecoder().decode(exponent)
            val n = BigInteger(1, decodeN)
            val e = BigInteger(1, decodeE)

            val keySpec = RSAPublicKeySpec(n, e)
            keyFactory.generatePublic(keySpec)
        } catch (e: NoSuchAlgorithmException) {
            throw SecurityException("RSA 알고리즘을 찾을 수 없습니다.", e)
        } catch (e: InvalidKeySpecException) {
            throw SecurityException("유효하지 않은 공개키 스펙입니다.", e)
        } catch (e: IllegalArgumentException) {
            throw SecurityException("잘못된 Base64 인코딩 값입니다.", e)
        }
    }
}

