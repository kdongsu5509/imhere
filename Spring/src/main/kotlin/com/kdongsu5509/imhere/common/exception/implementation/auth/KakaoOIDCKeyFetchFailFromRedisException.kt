package com.kdongsu5509.imhere.common.exception.implementation.auth

import com.kdongsu5509.imhere.common.exception.BaseException
import com.kdongsu5509.imhere.common.exception.ErrorCode

class KakaoOIDCKeyFetchFailFromRedisException(
    errorCode: ErrorCode = ErrorCode.KAKAO_OIDC_PUBLIC_KEY_FETCH_FROM_REDIS_FAILED,
): BaseException(
    errorCode,
    errorCode.message
)