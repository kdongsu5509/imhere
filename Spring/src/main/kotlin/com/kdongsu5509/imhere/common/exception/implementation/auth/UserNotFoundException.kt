package com.kdongsu5509.imhere.common.exception.implementation.auth

import com.kdongsu5509.imhere.common.exception.BaseException
import com.kdongsu5509.imhere.common.exception.ErrorCode

class UserNotFoundException(
    errorCode: ErrorCode = ErrorCode.USER_NOT_FOUND,
): BaseException(
    errorCode,
    errorCode.message
)