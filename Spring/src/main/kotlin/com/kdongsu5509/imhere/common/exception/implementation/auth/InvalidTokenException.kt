package com.kdongsu5509.imhere.common.exception.implementation.auth

import com.kdongsu5509.imhere.common.exception.BaseException
import com.kdongsu5509.imhere.common.exception.ErrorCode

class KakaoInvalidTokenException(
    errorCode: ErrorCode = ErrorCode.KAKAO_OIDC_ID_TOKEN_INVALID,
): BaseException(
    errorCode,
    errorCode.message
)