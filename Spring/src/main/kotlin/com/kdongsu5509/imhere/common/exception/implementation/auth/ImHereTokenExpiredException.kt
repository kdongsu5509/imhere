package com.kdongsu5509.imhere.common.exception.implementation.auth

import com.kdongsu5509.imhere.common.exception.BaseException
import com.kdongsu5509.imhere.common.exception.ErrorCode

class ImHereTokenExpiredException(
    errorCode: ErrorCode = ErrorCode.IMHERE_EXPIRED_TOKEN,
    detailMessage: String? = null
) : BaseException(
    errorCode,
    detailMessage ?: errorCode.message
)