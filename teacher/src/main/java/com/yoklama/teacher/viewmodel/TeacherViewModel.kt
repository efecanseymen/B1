package com.yoklama.teacher.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yoklama.teacher.data.model.*
import com.yoklama.teacher.data.repository.TeacherRepository
import com.yoklama.teacher.service.BleAdvertiserService
import kotlinx.coroutines.*

class TeacherViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = TeacherRepository()
    private val ctx get() = getApplication<Application>()

    // Auth state
    val loginResult  = MutableLiveData<LoginResponse?>()
    val errorMessage = MutableLiveData<String?>()
    var currentTeacherId: String? = null
    var currentTeacherName: String? = null

    // Session state
    val sessionStarted    = MutableLiveData<StartSessionResponse?>()
    val checkinTriggered  = MutableLiveData<TriggerCheckinResponse?>()
    val sessionEnded      = MutableLiveData<EndSessionResponse?>()
    val sessionReport     = MutableLiveData<SessionReportResponse?>()

    var currentSessionId: String? = null
    var currentCheckinId: String? = null
    var currentCourseCode: String? = null
    val checkinCount = MutableLiveData(0)
    val isSessionActive = MutableLiveData(false)

    private var autoCheckinJob: Job? = null

    // ---------- Auth ----------

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val r = repo.login(username, password)
                if (r.isSuccessful) {
                    val body = r.body()
                    if (body?.success == true && body.role == "teacher") {
                        currentTeacherId = body.userId
                        currentTeacherName = body.userName
                        loginResult.value = body
                    } else if (body?.role == "student") {
                        errorMessage.value = "Bu uygulama öğretmenler içindir."
                    } else {
                        loginResult.value = body
                    }
                } else {
                    errorMessage.value = "Giriş hatası: ${r.code()}"
                }
            } catch (e: Exception) {
                errorMessage.value = "Bağlantı hatası: ${e.message}"
            }
        }
    }

    fun clearLogin() {
        loginResult.value = null
        errorMessage.value = null
    }

    fun logout() {
        stopAutoCheckin()
        stopBle()
        loginResult.value = null
        currentTeacherId = null
        currentTeacherName = null
        currentSessionId = null
        isSessionActive.value = false
    }

    // ---------- Session ----------

    fun startSession(courseCode: String) {
        val teacherId = currentTeacherId ?: return
        viewModelScope.launch {
            try {
                val r = repo.startSession(teacherId, courseCode)
                if (r.isSuccessful && r.body()?.success == true) {
                    val body = r.body()!!
                    currentSessionId = body.session_id
                    currentCourseCode = courseCode
                    sessionStarted.value = body
                    isSessionActive.value = true
                    // İlk yoklamayı hemen al
                    triggerCheckin()
                } else {
                    errorMessage.value = "Ders başlatılamadı"
                }
            } catch (e: Exception) {
                errorMessage.value = "Bağlantı hatası: ${e.message}"
            }
        }
    }

    fun triggerCheckin() {
        val sessionId = currentSessionId ?: return
        viewModelScope.launch {
            try {
                val r = repo.triggerCheckin(sessionId)
                if (r.isSuccessful && r.body()?.success == true) {
                    val body = r.body()!!
                    currentCheckinId = body.checkin_id
                    checkinCount.value = body.checkin_number ?: 0
                    checkinTriggered.value = body
                    // BLE yayınını güncelle
                    updateBleCheckin(body.checkin_id ?: "")
                }
            } catch (e: Exception) {
                errorMessage.value = "Yoklama hatası: ${e.message}"
            }
        }
    }

    /** BLE yayınını başlat (startSession'dan sonra çağrılır) */
    fun startBle(sessionId: String, checkinId: String) {
        val intent = Intent(ctx, BleAdvertiserService::class.java).apply {
            putExtra(BleAdvertiserService.EXTRA_SESSION_ID, sessionId)
            putExtra(BleAdvertiserService.EXTRA_CHECKIN_ID, checkinId)
        }
        ctx.startForegroundService(intent)
    }

    private fun updateBleCheckin(checkinId: String) {
        val intent = Intent(ctx, BleAdvertiserService::class.java).apply {
            action = BleAdvertiserService.ACTION_UPDATE_CHECKIN
            putExtra(BleAdvertiserService.EXTRA_CHECKIN_ID, checkinId)
        }
        ctx.startService(intent)
    }

    private fun stopBle() {
        ctx.stopService(Intent(ctx, BleAdvertiserService::class.java))
    }

    /** Her 8 dakikada bir otomatik yoklama tetikler */
    fun startAutoCheckin() {
        autoCheckinJob = viewModelScope.launch {
            while (isActive) {
                delay(8 * 60 * 1000L) // 8 dakika
                if (isSessionActive.value == true) triggerCheckin()
            }
        }
    }

    fun stopAutoCheckin() {
        autoCheckinJob?.cancel()
        autoCheckinJob = null
    }

    fun endSession() {
        val sessionId = currentSessionId ?: return
        viewModelScope.launch {
            try {
                val r = repo.endSession(sessionId)
                if (r.isSuccessful) {
                    stopAutoCheckin()
                    stopBle()
                    isSessionActive.value = false
                    sessionEnded.value = r.body()
                }
            } catch (e: Exception) {
                errorMessage.value = "Ders bitirme hatası: ${e.message}"
            }
        }
    }

    fun loadReport(courseCode: String) {
        val teacherId = currentTeacherId ?: return
        viewModelScope.launch {
            try {
                val r = repo.getSessionReport(teacherId, courseCode)
                if (r.isSuccessful) sessionReport.value = r.body()
            } catch (e: Exception) {
                errorMessage.value = "Rapor yüklenemedi: ${e.message}"
            }
        }
    }
}
