package com.efecanseymen.b1.data.model

data class GetAttendanceResponse(
    val statusCode: Int?,
    val body: String?
) {
    val success: Boolean
        get() = statusCode == 200
}
