package com.kdongsu5509.imhere.auth.adapter.out.jjwt

import com.kdongsu5509.imhere.auth.application.dto.OIDCDecodePayload
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwt
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Component

/**
 * 카카오 OIDC JWT 토큰 파서
 * 카카오 OIDC ID 토큰의 파싱만 담당합니다. 검증은 하지 않습니다.
 *
 * 주요 기능:
 * - 토큰 구조 파싱 (헤더, 페이로드, 서명 분리)
 * - 서명되지 않은 토큰 헤더에서 kid 추출
 * - 페이로드 추출 (검증 없이)
 *
 * @see com.kdongsu5509.imhere.auth.application.service.oidc.KakaoOidcJwtTokenSignatureVerifier 토큰 서명 검증 담당
 */
@Component
class KakaoOidcJwtTokenParser {
    private val KID = "kid" // JWT 헤더의 kid 클레임 이름

    /**
     * 서명되지 않은 토큰 헤더에서 kid(Key ID) 추출
     *
     * @param token 카카오 OIDC ID 토큰
     * @param iss 발급자 (issuer) - 검증용
     * @param aud 대상 (audience) - 검증용
     * @return kid 값
     */
    fun getKidFromOriginTokenHeader(token: String, iss: String, aud: String): String {
        val parseClaimsJwt = Jwts.parserBuilder()
            .requireAudience(aud)
            .requireIssuer(iss)
            .build()
            .parseClaimsJwt(getUnsignedToken(token))
        return parseClaimsJwt.header[KID] as String
    }

    /**
     * 토큰의 서명 부분을 제거하여 서명되지 않은 형태의 토큰 문자열 반환
     *
     * @param token 원본 토큰
     * @return 서명이 제거된 토큰 (header.payload.)
     */
    private fun getUnsignedToken(token: String): String {
        val splitToken = token.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (splitToken.size != 3) {
            throw SecurityException("토큰 형식이 올바르지 않습니다. (header.payload.signature 형식이어야 합니다.)")
        }
        return "${splitToken[0]}.${splitToken[1]}."
    }

    /**
     * 검증된 JWS 객체에서 페이로드 추출
     *
     * @param jws 검증된 JWS 객체
     * @return OIDCDecodePayload 객체
     */
    fun extractPayloadFromJws(jws: Jws<Claims>): OIDCDecodePayload {
        val body = jws.body
        return OIDCDecodePayload(
            iss = body.issuer,
            aud = body.audience,
            sub = body.subject,
            email = body.get("email", String::class.java)
        )
    }
}