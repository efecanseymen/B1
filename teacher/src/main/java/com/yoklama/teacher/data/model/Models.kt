package com.yoklama.teacher.data.model

import com.google.gson.Gson

// --- Lambda Wrapper ---
data class LambdaWrapper(
    val statusCode: Int?,
    val body: String?
) {
    val isSuccess: Boolean get() = statusCode == 200 && body?.contains("\"success\": true") == true
    fun <T> parse(clazz: Class<T>): T? = try {
        body?.let { Gson().fromJson(it, clazz) }
    } catch (e: Exception) { null }
}

// --- Auth ---
data class LoginRequest(val username: String, val password: String)
data class LoginResponse(
    val statusCode: Int?,
    val body: String?
) {
    val success: Boolean get() = body?.contains("\"success\": true") == true
    val userId: String?   get() = body?.substringAfter("\"user_id\": \"")?.substringBefore("\"")
    val userName: String? get() = body?.substringAfter("\"user_name\": \"")?.substringBefore("\"")
    val role: String?     get() = body?.substringAfter("\"role\": \"")?.substringBefore("\"")
}

// --- Session ---
data class StartSessionRequest(val teacher_id: String, val course_code: String)
data class StartSessionBody(val success: Boolean, val session_id: String?, val message: String?)

data class TriggerCheckinRequest(val session_id: String)
data class TriggerCheckinBody(
    val success: Boolean,
    val checkin_id: String?,
    val checkin_number: Int?,
    val message: String?
)

data class EndSessionRequest(val session_id: String)

// Her öğrencinin o günkü sekan katılım sonucu
data class StudentResult(
    val student_id: String,
    val student_name: String,
    val checkins_attended: Int,  // o günkü katıldığı sekan sayısı
    val total_checkins: Int,     // o günkü toplam sekan
    val percentage: Double,      // sekan katılım %
    val status: String           // "present" veya "absent"
)

data class EndSessionBody(
    val success: Boolean,
    val session_id: String?,
    val course_code: String?,
    val session_date: String?,
    val total_checkins: Int?,
    val checkin_threshold: Double?,  // Kodda sabit sekan eşiği (örn. 70.0)
    val total_students: Int?,
    val present_count: Int?,
    val absent_count: Int?,
    val results: List<StudentResult>?,
    val message: String?
)

// --- Rapor ---
data class SessionReportRequest(
    val teacher_id: String,
    val course_code: String,
    val threshold: Double  // Öğretmenin girdiği devam eşiği
)

// Tek bir günün katılım kaydı
data class DailyAttendance(
    val date: String,
    val session_id: String,
    val status: String,      // "present" veya "absent"
    val percentage: Double   // o günkü sekan katılım %
)

// Öğrencinin kurs genelindeki raporu
data class StudentReportItem(
    val student_id: String,
    val student_name: String,
    val present_sessions: Int,
    val total_sessions: Int,
    val percentage: Double,
    val status: String,              // "passing" veya "failing"
    val daily_attendance: List<DailyAttendance>?
)

data class SessionReportBody(
    val success: Boolean,
    val course_code: String?,
    val total_sessions: Int?,
    val course_threshold: Double?,
    val students: List<StudentReportItem>?
)

// --- Kurslar ---
data class GetCoursesRequest(val teacher_id: String? = null, val student_id: String? = null)
data class CourseItem(val course_code: String, val course_name: String, val teacher_id: String)
data class GetCoursesBody(val success: Boolean, val courses: List<CourseItem>?)

// Alias'lar
typealias StartSessionResponse  = StartSessionBody
typealias TriggerCheckinResponse = TriggerCheckinBody
typealias EndSessionResponse    = EndSessionBody
typealias SessionReportResponse = SessionReportBody
typealias GetCoursesResponse    = GetCoursesBody
