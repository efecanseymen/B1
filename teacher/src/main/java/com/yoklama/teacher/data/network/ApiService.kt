package com.yoklama.teacher.data.network

import com.yoklama.teacher.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("start-session")
    suspend fun startSession(@Body request: StartSessionRequest): Response<LambdaWrapper>

    @POST("trigger-checkin")
    suspend fun triggerCheckin(@Body request: TriggerCheckinRequest): Response<LambdaWrapper>

    @POST("end-session")
    suspend fun endSession(@Body request: EndSessionRequest): Response<LambdaWrapper>

    @POST("get-session-report")
    suspend fun getSessionReport(@Body request: SessionReportRequest): Response<LambdaWrapper>

    @POST("get-courses")
    suspend fun getCourses(@Body request: GetCoursesRequest): Response<LambdaWrapper>
}
