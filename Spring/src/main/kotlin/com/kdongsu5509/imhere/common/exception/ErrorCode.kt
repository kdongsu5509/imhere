package com.kdongsu5509.imhere.common.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val code: String,
    val message: String
) {
    /**
     * KAKAO OIDC ERRORS : 카카오 OIDC 관련 오류
     */
    KAKAO_OIDC_PUBLIC_KEY_FETCH_FAILED(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "AUTH_001_KAKAO",
        "카카오 OIDC 공개 키를 가져오는데 실패했습니다."
    ),
    KAKAO_OIDC_ID_TOKEN_INVALID(
        HttpStatus.UNAUTHORIZED,
        "AUTH_002_KAKAO",
        "카카오 OIDC ID 토큰이 유효하지 않습니다."
    ),
    /**
     * USER ERRORS : 사용자 관련 오류
     */
    USER_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "USER_001",
        "사용자를 찾을 수 없습니다."
    ),

    /**
     * INTERNAL SERVER ERRORS : 알 수 없는 서버 오류
     */
    UNKNOWN_ERROR(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "UNKNOWN_INTERNAL_SERVER",
        "알 수 없는 오류가 발생했습니다."
    )
}