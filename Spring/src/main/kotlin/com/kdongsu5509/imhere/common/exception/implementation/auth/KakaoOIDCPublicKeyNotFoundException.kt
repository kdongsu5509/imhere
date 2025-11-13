package com.kdongsu5509.imhere.common.exception.implementation.auth

import com.kdongsu5509.imhere.common.exception.BaseException
import com.kdongsu5509.imhere.common.exception.ErrorCode

class KakaoOIDCPublicKeyNotFoundException(
    errorCode: ErrorCode = ErrorCode.KAKAO_OIDC_PUBLIC_KEY_NOT_FOUND
) : BaseException(
    errorCode,
    errorCode.message
)