package com.kdongsu5509.imhere.common.exception.implementation.auth

import com.kdongsu5509.imhere.common.exception.BaseException
import com.kdongsu5509.imhere.common.exception.ErrorCode

class KakaoFetchFailException(
    errorCode: ErrorCode = ErrorCode.KAKAO_OIDC_PUBLIC_KEY_FETCH_FAILED,
): BaseException(
    errorCode,
    errorCode.message
)