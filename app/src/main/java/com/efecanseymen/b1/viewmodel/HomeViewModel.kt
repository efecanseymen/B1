package com.efecanseymen.b1.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.efecanseymen.b1.data.model.LoginResponse
import com.efecanseymen.b1.data.model.SyncResponse
import com.efecanseymen.b1.data.repository.AttendanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository = AttendanceRepository()

    val loginResult = MutableLiveData<LoginResponse?>()
    val syncResult = MutableLiveData<SyncResponse?>()
    val errorMessage = MutableLiveData<String>()
    val isScanning = MutableStateFlow(false)

    var currentUserId: String? = null
    var currentUserName: String? = null

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.login(username, password)
                if (response.isSuccessful) {
                    val body = response.body()
                    currentUserId = body?.userId
                    currentUserName = body?.userName
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
    }

    fun clearLoginResult() {
        loginResult.value = null
        errorMessage.value = null
    }
}