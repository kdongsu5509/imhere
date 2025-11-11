package com.kdongsu5509.imhere.auth.application.service.oidc

import com.kdongsu5509.imhere.auth.adapter.out.dto.OIDCPublicKeyResponse
import com.kdongsu5509.imhere.auth.application.port.out.CachePort
import io.jsonwebtoken.MalformedJwtException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 카카오 OAuth OIDC ID Token를 프론트엔드로부터 받아서 검증하는 서비스
 * 카카오 공식 문서 기반 검증 규격을 준수하여 토큰의 유효성을 검증합니다.
 *
 * 검증 프로세스:
 * 1. 공개키 조회 (캐시에서)
 * 2. 토큰 파싱 및 서명 검증 (KakaoOidcTokenVerificationHelper)
 * 3. 페이로드 검증 (KakaoOidcIdTokenPayloadVerifier)
 *
 * @see KakaoOidcTokenVerificationHelper 토큰 파싱 및 서명 검증 헬퍼
 * @see KakaoOidcIdTokenPayloadVerifier 페이로드 검증 담당
 */
@Service
@Transactional
class KakaoOIDCVerifier(
    private val cachePort: CachePort,
    private val kakaoOidcTokenVerificationHelper: KakaoOidcTokenVerificationHelper,
    private val kakaoOidcIdTokenPayloadVerifier: KakaoOidcIdTokenPayloadVerifier
) {

    // 💡 검증에 필요한 상수 (카카오 문서 기반)
    companion object {
        private const val KAKAO_ISSUER = "https://kauth.kakao.com"
        private const val KAKAO_AUDIENCE = "bf284f33bfeba9bc59575706d0eb0e9c"
        private const val CACHE_KEY = "kakaoOidcKeys::kakaoPublicKeySet"
    }

    /**
     * 프론트엔드로부터 받은 카카오 OIDC ID 토큰을 검증합니다.
     *
     * @param idToken 프론트엔드로부터 받은 카카오 OIDC ID 토큰
     * @return 검증 성공 시 true 반환
     * @throws SecurityException 토큰 검증 실패 시 예외 발생
     */
    fun verifyAndReturnEmail(idToken: String): String {
        try {
            // 1. Redis 캐시에서 공개키 목록 조회
            val cachedKeySet = getCachedPublicKeys()

            // 2. KakaoOidcTokenVerificationHelper를 통해 토큰 검증 및 페이로드 추출
            //    내부적으로 KakaoOidcJwtTokenParser를 사용하여 kid 추출, 공개키 찾기, 서명 검증을 수행
            val payload = kakaoOidcTokenVerificationHelper.getPayloadFromIdToken(
                idToken,
                KAKAO_ISSUER,
                KAKAO_AUDIENCE,
                cachedKeySet
            )

            // 3. 페이로드 검증 (iss, aud, exp, nonce 등)
            kakaoOidcIdTokenPayloadVerifier.verifyPayload(
                payload,
                KAKAO_AUDIENCE
            )

            return payload.email ?: throw MalformedJwtException("null을 허용하지 않습니다")
        } catch (e: SecurityException) {
            throw e
        } catch (e: Exception) {
            throw SecurityException("ID 토큰 검증에 실패했습니다. (${e.message})", e)
        }
    }

    private fun getCachedPublicKeys(): OIDCPublicKeyResponse {
        val cachedKeySet = cachePort.find(CACHE_KEY) as? OIDCPublicKeyResponse
            ?: throw SecurityException("공개키 캐시가 비어있습니다. 카카오 서버에 요청하여 초기화가 필요합니다.")
        return cachedKeySet
    }
}