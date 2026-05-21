package com.efecanseymen.b1.data.network

import com.efecanseymen.b1.data.model.CreateUserRequest
import com.efecanseymen.b1.data.model.CreateUserResponse
import com.efecanseymen.b1.data.model.GetAttendanceRequest
import com.efecanseymen.b1.data.model.GetAttendanceResponse
import com.efecanseymen.b1.data.model.LoginRequest
import com.efecanseymen.b1.data.model.LoginResponse
import com.efecanseymen.b1.data.model.SyncRequest
import com.efecanseymen.b1.data.model.SyncResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("sync")
    suspend fun sync(@Body request: SyncRequest): Response<SyncResponse>

    @POST("create-user")
    suspend fun createUser(@Body request: CreateUserRequest): Response<CreateUserResponse>

    @POST("get-attendance")
    suspend fun getAttendance(@Body request: GetAttendanceRequest): Response<GetAttendanceResponse>
}