package com.efecanseymen.b1.data.network

import com.efecanseymen.b1.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("sync")
    suspend fun sync(@Body request: SyncRequest): Response<SyncResponse>

    @POST("create-user")
    suspend fun createUser(@Body request: CreateUserRequest): Response<LambdaWrapper>

    @POST("get-attendance")
    suspend fun getAttendance(@Body request: GetAttendanceRequest): Response<LambdaWrapper>

    // getCourses → wrapper döner, body içinde StudentCoursesBody var
    @POST("get-courses")
    suspend fun getCourses(@Body request: GetCoursesRequest): Response<LambdaWrapper>

    // report-presence → wrapper döner, body içinde ReportPresenceBody var
    @POST("report-presence")
    suspend fun reportPresence(@Body request: ReportPresenceRequest): Response<LambdaWrapper>
}