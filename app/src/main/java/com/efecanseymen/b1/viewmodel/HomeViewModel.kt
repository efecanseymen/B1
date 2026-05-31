package com.efecanseymen.b1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.efecanseymen.b1.data.model.*
import com.efecanseymen.b1.data.repository.AttendanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AttendanceRepository()

    val loginResult    = MutableLiveData<LoginResponse?>()
    val errorMessage   = MutableLiveData<String?>()
    val isScanning     = MutableStateFlow(false)
    val courses        = MutableLiveData<List<StudentCourseInfo>>(emptyList())
    val isLoadingCourses = MutableLiveData(false)
    val presenceReported = MutableLiveData<String?>() // son bildirilen checkin_id
    val createUserResult = MutableLiveData<CreateUserResponse?>()

    var currentUserId: String?   = null
    var currentUserName: String? = null
    var currentUserRole: String? = null

    // Aynı checkin_id için tekrar istek göndermeyi önler
    private val reportedCheckins = mutableSetOf<String>()

    // ---------- Auth ----------

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.login(username, password)
                if (response.isSuccessful) {
                    val body = response.body()
                    currentUserId   = body?.userId
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

    fun logout() {
        loginResult.value = null
        currentUserId = null; currentUserName = null; currentUserRole = null
        courses.value = emptyList()
        reportedCheckins.clear()
    }

    fun clearLoginResult() {
        loginResult.value = null
        errorMessage.value = null
    }

    fun createUser(userName: String, password: String, role: String, userId: String?, email: String?) {
        viewModelScope.launch {
            try {
                val response = repository.createUser(userName, password, role, userId, email)
                if (response.isSuccessful) {
                    createUserResult.value = response.body()
                } else {
                    errorMessage.value = "Hesap oluşturma hatası: ${response.code()}"
                    createUserResult.value = CreateUserResponse(success = false, message = "Hata")
                }
            } catch (e: Exception) {
                errorMessage.value = "Bağlantı hatası: ${e.message}"
                createUserResult.value = CreateUserResponse(success = false, message = e.message ?: "Hata")
            }
        }
    }

    fun clearCreateUserResult() {
        createUserResult.value = null
    }

    // ---------- Dersler ----------

    fun loadCourses() {
        val uid = currentUserId ?: run {
            errorMessage.value = "Kullanıcı oturumu bulunamadı"
            return
        }
        isLoadingCourses.value = true
        viewModelScope.launch {
            try {
                val r = repository.getCourses(uid)
                if (r.isSuccessful && r.body()?.success == true) {
                    courses.value = r.body()?.courses ?: emptyList()
                } else {
                    val errBody = r.errorBody()?.string() ?: r.body()?.toString() ?: ""
                    errorMessage.value = "Yükleme hatası (HTTP ${r.code()}): $errBody"
                }
            } catch (e: Exception) {
                errorMessage.value = "Bağlantı hatası: ${e.message}"
            } finally {
                isLoadingCourses.value = false
            }
        }
    }

    // ---------- BLE Presence ----------

    fun reportPresence(sessionId: String, checkinId: String) {
        val uid = currentUserId ?: return
        if (checkinId in reportedCheckins) return // tekrar gönderme
        reportedCheckins.add(checkinId)
        viewModelScope.launch {
            try {
                val r = repository.reportPresence(uid, checkinId, sessionId)
                if (r.isSuccessful && r.body()?.success == true) {
                    presenceReported.value = checkinId
                } else {
                    reportedCheckins.remove(checkinId) // başarısız → tekrar dene
                }
            } catch (e: Exception) {
                reportedCheckins.remove(checkinId)
                errorMessage.value = "Bağlantı hatası: ${e.message}"
            }
        }
    }

    fun startScan()  { isScanning.value = true  }
    fun stopScan()   { isScanning.value = false }
}