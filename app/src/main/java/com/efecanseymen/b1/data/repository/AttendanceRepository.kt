package com.efecanseymen.b1.data.repository

import com.efecanseymen.b1.data.model.*
import com.efecanseymen.b1.data.network.RetrofitInstance
import retrofit2.Response

class AttendanceRepository {

    private val api = RetrofitInstance.api

    suspend fun login(username: String, password: String): Response<LoginResponse> {
        return api.login(LoginRequest(username, hashSha256(password)))
    }

    suspend fun createUser(
        userName: String, password: String, role: String = "student",
        userId: String? = null, email: String? = null
    ): CreateUserResponse {
        val r = api.createUser(
            CreateUserRequest(userName, hashSha256(password), role, userId, email)
        )
        return r.body()?.parse(CreateUserResponse::class.java)
            ?: CreateUserResponse(false, "Sunucu hatası")
    }

    suspend fun getCourses(studentId: String): StudentCoursesBody? {
        val r = api.getCourses(GetCoursesRequest(studentId))
        if (!r.isSuccessful) return null
        return r.body()?.parse(StudentCoursesBody::class.java)
    }

    suspend fun reportPresence(studentId: String, checkinId: String, sessionId: String): ReportPresenceBody? {
        val r = api.reportPresence(ReportPresenceRequest(studentId, checkinId, sessionId))
        if (!r.isSuccessful) return null
        return r.body()?.parse(ReportPresenceBody::class.java)
    }

    private fun hashSha256(input: String): String {
        val bytes = java.security.MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}