package com.yoklama.teacher.data.repository

import com.yoklama.teacher.data.model.*
import com.yoklama.teacher.data.network.RetrofitInstance

class TeacherRepository {
    private val api = RetrofitInstance.api

    suspend fun login(username: String, password: String): LoginResponse? {
        val r = api.login(LoginRequest(username, hashSha256(password)))
        return if (r.isSuccessful) r.body() else null
    }

    suspend fun startSession(teacherId: String, courseCode: String): StartSessionBody? {
        val r = api.startSession(StartSessionRequest(teacherId, courseCode))
        return if (r.isSuccessful) r.body()?.parse(StartSessionBody::class.java) else null
    }

    suspend fun triggerCheckin(sessionId: String): TriggerCheckinBody? {
        val r = api.triggerCheckin(TriggerCheckinRequest(sessionId))
        return if (r.isSuccessful) r.body()?.parse(TriggerCheckinBody::class.java) else null
    }

    suspend fun endSession(sessionId: String): EndSessionBody? {
        val r = api.endSession(EndSessionRequest(sessionId))
        return if (r.isSuccessful) r.body()?.parse(EndSessionBody::class.java) else null
    }

    suspend fun getSessionReport(teacherId: String, courseCode: String): SessionReportBody? {
        val r = api.getSessionReport(SessionReportRequest(teacherId, courseCode))
        return if (r.isSuccessful) r.body()?.parse(SessionReportBody::class.java) else null
    }

    suspend fun getCoursesByTeacher(teacherId: String): GetCoursesBody? {
        val r = api.getCourses(GetCoursesRequest(teacher_id = teacherId))
        return if (r.isSuccessful) r.body()?.parse(GetCoursesBody::class.java) else null
    }

    private fun hashSha256(input: String): String {
        val bytes = java.security.MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
