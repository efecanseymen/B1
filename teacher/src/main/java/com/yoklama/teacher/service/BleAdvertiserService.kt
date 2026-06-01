package com.yoklama.teacher.service

import android.app.*
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.ParcelUuid
import android.util.Log
import androidx.core.app.NotificationCompat

/**
 * ForegroundService: Öğretmen telefonu BLE yayını yapar.
 * Payload: "SESSION_ID|CHECKIN_ID" formatında ASCII bytes olarak gönderilir.
 * Öğrenci uygulaması bu UUID'yi tarar ve payload'dan session/checkin bilgisini okur.
 */
class BleAdvertiserService : Service() {

    private var advertiser: BluetoothLeAdvertiser? = null
    private var currentCallback: AdvertiseCallback? = null

    var sessionId: String = ""
    var checkinId: String = ""

    companion object {
        const val EXTRA_SESSION_ID = "session_id"
        const val EXTRA_CHECKIN_ID = "checkin_id"
        const val ACTION_UPDATE_CHECKIN = "com.yoklama.teacher.UPDATE_CHECKIN"
        const val CHANNEL_ID = "BLE_YOKLAMA_CHANNEL"
        const val NOTIF_ID = 1001
        val SERVICE_UUID: ParcelUuid =
            ParcelUuid.fromString("0000FEF5-0000-1000-8000-00805F9B34FB")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_UPDATE_CHECKIN -> {
                checkinId = intent.getStringExtra(EXTRA_CHECKIN_ID) ?: checkinId
                Log.d("BLE", "Checkin güncellendi: $checkinId")
                restartAdvertising()
            }
            else -> {
                sessionId = intent?.getStringExtra(EXTRA_SESSION_ID) ?: ""
                checkinId = intent?.getStringExtra(EXTRA_CHECKIN_ID) ?: ""
                startForeground(NOTIF_ID, buildNotification())
                startAdvertising()
            }
        }
        return START_STICKY
    }

    private fun startAdvertising() {
        val btManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        advertiser = btManager.adapter.bluetoothLeAdvertiser

        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .setConnectable(false)
            .build()

        val data = buildData()

        currentCallback = object : AdvertiseCallback() {
            override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
                Log.d("BLE", "Yayın başladı: $sessionId | $checkinId")
            }
            override fun onStartFailure(errorCode: Int) {
                Log.e("BLE", "Yayın başlatılamadı, hata: $errorCode")
            }
        }
        advertiser?.startAdvertising(settings, data, currentCallback)
    }

    private fun restartAdvertising() {
        currentCallback?.let { advertiser?.stopAdvertising(it) }
        startAdvertising()
    }

    private fun buildData(): AdvertiseData {
        val payload = "$sessionId|$checkinId"
        val bytes = payload.toByteArray(Charsets.UTF_8)
        return AdvertiseData.Builder()
            .addServiceUuid(SERVICE_UUID)
            .addServiceData(SERVICE_UUID, bytes)
            .setIncludeDeviceName(false)
            .build()
    }

    private fun buildNotification(): Notification {
        val channel = NotificationChannel(
            CHANNEL_ID, "Yoklama BLE Yayını", NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Yoklama Aktif")
            .setContentText("Ders devam ediyor — Bluetooth yayını açık")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .build()
    }

    override fun onDestroy() {
        currentCallback?.let { advertiser?.stopAdvertising(it) }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
