package com.kdongsu5509.imhere.common.exception.implementation.auth

import com.kdongsu5509.imhere.common.exception.BaseException
import com.kdongsu5509.imhere.common.exception.ErrorCode

class OIDCExpiredException(
    errorCode: ErrorCode = ErrorCode.OIDC_EXPIRED,
    detailMessage: String? = null
) : BaseException(
    errorCode,
    detailMessage ?: errorCode.message
)