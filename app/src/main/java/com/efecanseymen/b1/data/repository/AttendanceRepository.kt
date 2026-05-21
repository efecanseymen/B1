package com.efecanseymen.b1.data.repository

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

    private fun hashSha256(input: String): String {
        val bytes = java.security.MessageDigest
            .getInstance("SHA-256")
            .digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}