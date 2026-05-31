package com.efecanseymen.b1.data.repository

import com.efecanseymen.b1.data.model.*
import com.efecanseymen.b1.data.network.RetrofitInstance
import retrofit2.Response

class AttendanceRepository {

    private val api = RetrofitInstance.api

    suspend fun login(username: String, password: String): Response<LoginResponse> {
        val hashedPassword = hashSha256(password)
        return api.login(LoginRequest(username, hashedPassword))
    }

    suspend fun syncAttendance(userId: String, sessionId: String, timestamp: String): Response<SyncResponse> =
        api.sync(SyncRequest(userId, sessionId, timestamp))

    suspend fun createUser(
        userName: String, password: String, role: String = "student",
        userId: String? = null, email: String? = null
    ): Response<CreateUserResponse> =
        api.createUser(CreateUserRequest(userName, hashSha256(password), role, userId, email))

    suspend fun getAttendance(role: String, studentId: String? = null, sessionId: String? = null): Response<GetAttendanceResponse> =
        api.getAttendance(GetAttendanceRequest(role, studentId, sessionId))

    suspend fun getCourses(studentId: String): Response<StudentCoursesResponse> =
        api.getCourses(GetCoursesRequest(studentId))

    suspend fun reportPresence(studentId: String, checkinId: String, sessionId: String): Response<ReportPresenceResponse> =
        api.reportPresence(ReportPresenceRequest(studentId, checkinId, sessionId))

    private fun hashSha256(input: String): String {
        val bytes = java.security.MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}