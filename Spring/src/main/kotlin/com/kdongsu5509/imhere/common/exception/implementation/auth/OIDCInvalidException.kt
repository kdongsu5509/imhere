package com.kdongsu5509.imhere.common.exception.implementation.auth

import com.kdongsu5509.imhere.common.exception.BaseException
import com.kdongsu5509.imhere.common.exception.ErrorCode

class OIDCInvalidException(
    errorCode: ErrorCode = ErrorCode.OIDC_INVALID,
    detailMessage: String? = null
) : BaseException(
    errorCode,
    detailMessage ?: errorCode.message
)