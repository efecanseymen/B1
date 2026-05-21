package com.efecanseymen.b1.data.model

data class CreateUserResponse(
    val statusCode: Int?,
    val body: String?
) {
    val success: Boolean
        get() = body?.contains("\"success\": true") == true ||
                body?.contains("\"success\":true") == true

    val userId: String?
        get() = body
            ?.substringAfter("\"user_id\": \"", "")
            ?.substringBefore("\"")
            ?.ifEmpty { null }
}
