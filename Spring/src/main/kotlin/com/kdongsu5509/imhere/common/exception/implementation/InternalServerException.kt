package com.kdongsu5509.imhere.common.exception.implementation

import com.kdongsu5509.imhere.common.exception.BaseException
import com.kdongsu5509.imhere.common.exception.ErrorCode

class InternalServerException(
    errorCode: ErrorCode = ErrorCode.UNKNOWN_ERROR,
): BaseException(
    errorCode,
    errorCode.message
)