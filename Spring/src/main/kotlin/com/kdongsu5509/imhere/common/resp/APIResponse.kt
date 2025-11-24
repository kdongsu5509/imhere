package com.kdongsu5509.imhere.common.resp

import org.springframework.http.HttpStatus

class APIResponse<T> private constructor(
    val code: Int,
    val message: String?,
    val data: T?
) {
    companion object {
        fun <T> success(data: T?): APIResponse<T?> {
            return APIResponse(
                HttpStatus.OK.value(),
                HttpStatus.OK.reasonPhrase,
                data
            )
        }

        fun success(): APIResponse<Unit> {
            return APIResponse(
                HttpStatus.OK.value(),
                "OK",
                null
            )
        }

        fun <T> fail(code: Int, message: String?): APIResponse<T?> {
            return APIResponse(code, message, null)
        }
    }
}