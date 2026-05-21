package com.efecanseymen.b1.data.repository

import com.efecanseymen.b1.data.model.CreateUserRequest
import com.efecanseymen.b1.data.model.CreateUserResponse
import com.efecanseymen.b1.data.model.GetAttendanceRequest
import com.efecanseymen.b1.data.model.GetAttendanceResponse
import com.efecanseymen.b1.data.model.LoginRequest
import com.efecanseymen.b1.data.model.LoginResponse
import com.efecanseymen.b1.data.model.SyncRequest
import com.efecanseymen.b1.data.model.SyncResponse
import com.efecanseymen.b1.data.network.RetrofitInstance
import retrofit2.Response

class AttendanceRepository {

    private val api = RetrofitInstance.api

    suspend fun login(username: String, password: String): Response<LoginResponse> {
        val hashedPassword = hashSha256(password)
        return api.login(LoginRequest(username, hashedPassword))
    }

    suspend fun syncAttendance(
        userId: String,
        sessionId: String,
        timestamp: String
    ): Response<SyncResponse> {
        return api.sync(SyncRequest(userId, sessionId, timestamp))
    }

    suspend fun createUser(
        userName: String,
        password: String,
        role: String = "student",
        userId: String? = null,
        email: String? = null
    ): Response<CreateUserResponse> {
        val hashedPassword = hashSha256(password)
        return api.createUser(
            CreateUserRequest(
                user_name = userName,
                password = hashedPassword,
                role = role,
                user_id = userId,
                email = email
            )
        )
    }

    suspend fun getAttendance(
        role: String,
        studentId: String? = null,
        sessionId: String? = null
    ): Response<GetAttendanceResponse> {
        return api.getAttendance(
            GetAttendanceRequest(
                role = role,
                student_id = studentId,
                session_id = sessionId
            )
        )
    }

    private fun hashSha256(input: String): String {
        val bytes = java.security.MessageDigest
            .getInstance("SHA-256")
            .digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}