package com.kdongsu5509.imhere.auth.application.service

import com.kdongsu5509.imhere.auth.adapter.out.dto.OIDCPublicKey
import com.kdongsu5509.imhere.auth.adapter.out.dto.OIDCPublicKeyResponse
import com.kdongsu5509.imhere.auth.application.dto.OIDCDecodePayload
import org.springframework.stereotype.Component

/**
 * 카카오 OIDC 토큰 검증 헬퍼
 * 카카오 OIDC ID 토큰의 검증 프로세스를 관리하는 중간 관리자 역할을 수행합니다.
 * 
 * 주요 기능:
 * - 토큰 헤더에서 kid(Key ID) 추출 (파서 사용)
 * - 공개키 목록에서 kid에 해당하는 공개키 찾기
 * - 공개키를 사용한 토큰 서명 검증 (검증기 사용)
 * - 검증된 페이로드 추출 (파서 사용)
 * 
 * @see KakaoOidcJwtTokenParser 토큰 파싱 담당
 * @see KakaoOidcJwtTokenSignatureVerifier 토큰 서명 검증 담당
 */
@Component
class KakaoOidcTokenVerificationHelper(
    private val kakaoOidcJwtTokenParser: KakaoOidcJwtTokenParser,
    private val kakaoOidcJwtTokenSignatureVerifier: KakaoOidcJwtTokenSignatureVerifier
) {

    /**
     * ID 토큰으로부터 OIDC 페이로드 검증 및 추출
     * 
     * @param token 카카오 OIDC ID 토큰
     * @param iss 발급자 (issuer) - https://kauth.kakao.com
     * @param aud 대상 (audience) - 앱 키
     * @param oidcPublicKeysResponse 공개키 목록 응답
     * @return 검증된 페이로드
     * @throws SecurityException 토큰 검증 실패 시
     */
    fun getPayloadFromIdToken(
        token: String, 
        iss: String, 
        aud: String, 
        oidcPublicKeysResponse: OIDCPublicKeyResponse
    ): OIDCDecodePayload {
        // 1. 토큰 헤더에서 kid 추출 (파서 사용)
        val kid = kakaoOidcJwtTokenParser.getKidFromUnsignedTokenHeader(token, iss, aud)

        // 2. 공개키 목록에서 kid에 해당하는 공개키 찾기
        val oidcPublicKey: OIDCPublicKey =
            oidcPublicKeysResponse.keys.firstOrNull { it.kid == kid }
                ?: throw SecurityException("캐시된 키 목록에 kid: $kid 에 해당하는 키가 없습니다.")

        // 3. 공개키를 사용하여 토큰 서명 검증 (검증기 사용)
        val jws = kakaoOidcJwtTokenSignatureVerifier.verifyTokenSignature(
            token,
            oidcPublicKey.n,
            oidcPublicKey.e
        )

        // 4. 검증된 페이로드 추출 (파서 사용)
        return kakaoOidcJwtTokenParser.extractPayloadFromJws(jws)
    }
}