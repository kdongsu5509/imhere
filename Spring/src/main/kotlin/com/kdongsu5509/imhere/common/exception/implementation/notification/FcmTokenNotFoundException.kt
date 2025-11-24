package com.kdongsu5509.imhere.common.exception.implementation.notification

import com.kdongsu5509.imhere.common.exception.BaseException
import com.kdongsu5509.imhere.common.exception.ErrorCode

class FcmTokenNotFoundException(
    errorCode: ErrorCode = ErrorCode.FCM_TOKEN_NOT_FOUND,
) : BaseException(
    errorCode,
    errorCode.message
)