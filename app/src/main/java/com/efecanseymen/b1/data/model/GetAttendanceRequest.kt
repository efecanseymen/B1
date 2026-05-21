package com.efecanseymen.b1.data.model

data class GetAttendanceRequest(
    val role: String,
    val student_id: String? = null,
    val session_id: String? = null
)
