package com.efecanseymen.b1.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(val username: String, val password: String)

data class LoginResponse(
    val statusCode: Int?,
    val body: String?
) {
    val success: Boolean get() = body?.contains("\"success\": true") == true
    val userId: String? get() = body?.substringAfter("\"user_id\": \"")?.substringBefore("\"")
    val userName: String? get() = body?.substringAfter("\"user_name\": \"")?.substringBefore("\"")
    val role: String? get() = body?.substringAfter("\"role\": \"")?.substringBefore("\"")
}

data class SyncRequest(val userId: String, val sessionId: String, val timestamp: String)
data class SyncResponse(val success: Boolean, val message: String?)

data class CreateUserRequest(
    val user_name: String,
    val password: String,
    val role: String,
    val user_id: String?,
    val email: String?
)
data class CreateUserResponse(val success: Boolean, val message: String?)

data class GetAttendanceRequest(val role: String, val student_id: String?, val session_id: String?)
data class GetAttendanceResponse(val success: Boolean, val data: Any?)

// --- Yeni modeller ---

data class GetCoursesRequest(val student_id: String)

data class StudentAttendanceItem(
    val session_id: String,
    val date: String,
    val status: String,
    val percentage: Double
)

data class StudentCourseInfo(
    val course_code: String,
    val course_name: String,
    val teacher_id: String,
    val present_sessions: Int,
    val total_sessions: Int,
    val overall_percentage: Double,
    val attendance: List<StudentAttendanceItem>?
)

data class StudentCoursesResponse(
    val success: Boolean,
    val courses: List<StudentCourseInfo>?
)

data class ReportPresenceRequest(
    val student_id: String,
    val checkin_id: String,
    val session_id: String
)

data class ReportPresenceResponse(
    val success: Boolean,
    val message: String?
)
