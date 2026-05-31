package com.efecanseymen.b1.data.model

data class SyncRequest(
    val userId: String,
    val sessionId: String,
    val timestamp: String
)