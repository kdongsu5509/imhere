package com.kdongsu5509.imhere.common.exception.implementation.auth

import com.kdongsu5509.imhere.common.exception.BaseException
import com.kdongsu5509.imhere.common.exception.ErrorCode

class ImHereTokenInvalidException(
    errorCode: ErrorCode = ErrorCode.IMHERE_INVALID_TOKEN,
    detailMessage: String? = null
) : BaseException(
    errorCode,
    detailMessage ?: errorCode.message
)