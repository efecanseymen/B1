package com.efecanseymen.b1.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.efecanseymen.b1.data.model.CreateUserResponse
import com.efecanseymen.b1.data.model.GetAttendanceResponse
import com.efecanseymen.b1.data.model.LoginResponse
import com.efecanseymen.b1.data.model.SyncResponse
import com.efecanseymen.b1.data.repository.AttendanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository = AttendanceRepository()

    val loginResult = MutableLiveData<LoginResponse?>()
    val syncResult = MutableLiveData<SyncResponse?>()
    val createUserResult = MutableLiveData<CreateUserResponse?>()
    val getAttendanceResult = MutableLiveData<GetAttendanceResponse?>()
    val errorMessage = MutableLiveData<String>()
    val isScanning = MutableStateFlow(false)

    var currentUserId: String? = null
    var currentUserName: String? = null
    var currentUserRole: String? = null

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.login(username, password)
                if (response.isSuccessful) {
                    val body = response.body()
                    currentUserId = body?.userId
                    currentUserName = body?.userName
                    currentUserRole = body?.role
                    loginResult.value = body
                } else {
                    errorMessage.value = "Hata: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage.value = "Bağlantı hatası: ${e.message}"
            }
        }
    }

    fun syncAttendance(userId: String, sessionId: String, timestamp: String) {
        viewModelScope.launch {
            try {
                val response = repository.syncAttendance(userId, sessionId, timestamp)
                if (response.isSuccessful) {
                    syncResult.value = response.body()
                } else {
                    errorMessage.value = "Sync hatası: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage.value = "Bağlantı hatası: ${e.message}"
            }
        }
    }

    fun startScan() {
        isScanning.value = true
    }

    fun stopScan() {
        isScanning.value = false
    }

    fun logout() {
        loginResult.value = null
        currentUserId = null
        currentUserName = null
        currentUserRole = null
    }

    fun clearLoginResult() {
        loginResult.value = null
        errorMessage.value = null
    }

    fun createUser(
        userName: String,
        password: String,
        role: String = "student",
        userId: String? = null,
        email: String? = null
    ) {
        viewModelScope.launch {
            try {
                val response = repository.createUser(userName, password, role, userId, email)
                if (response.isSuccessful) {
                    createUserResult.value = response.body()
                } else {
                    errorMessage.value = "Kayıt hatası: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage.value = "Bağlantı hatası: ${e.message}"
            }
        }
    }

    fun getAttendance(role: String, studentId: String? = null, sessionId: String? = null) {
        viewModelScope.launch {
            try {
                val response = repository.getAttendance(role, studentId, sessionId)
                if (response.isSuccessful) {
                    getAttendanceResult.value = response.body()
                } else {
                    errorMessage.value = "Yoklama sorgu hatası: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage.value = "Bağlantı hatası: ${e.message}"
            }
        }
    }

    fun clearCreateUserResult() {
        createUserResult.value = null
    }
}