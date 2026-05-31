package com.efecanseymen.b1.ui.screens

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.efecanseymen.b1.service.BleScannerService
import com.efecanseymen.b1.viewmodel.HomeViewModel

@Composable
fun ScanScreen(viewModel: HomeViewModel, modifier: Modifier) {
    val context = LocalContext.current
    var isScanning by remember { mutableStateOf(false) }
    var lastCheckinId by remember { mutableStateOf<String?>(null) }
    var reportSuccess by remember { mutableStateOf<Boolean?>(null) }
    val presenceReported by viewModel.presenceReported.observeAsState()

    // BLE broadcast dinle
    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                if (intent.action == BleScannerService.ACTION_PRESENCE_REPORTED) {
                    lastCheckinId = intent.getStringExtra(BleScannerService.EXTRA_CHECKIN_ID)
                    reportSuccess = intent.getBooleanExtra(BleScannerService.EXTRA_STATUS, false)
                }
            }
        }
        val filter = IntentFilter(BleScannerService.ACTION_PRESENCE_REPORTED)
        ContextCompat.registerReceiver(context, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
        onDispose { context.unregisterReceiver(receiver) }
    }

    // İzin launcher
    val permissions = if (Build.VERSION.SDK_INT >= 31)
        arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION)
    else
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { granted ->
        if (granted.values.all { it }) startBleService(context, viewModel, isScanning = true)
            .also { isScanning = true }
    }

    // Pulse animasyonu (taranırken)
    val infiniteTransition = rememberInfiniteTransition(label = "rings")
    val ring1Scale by infiniteTransition.animateFloat(
        0.6f, 2f, infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Restart), label = "r1s")
    val ring1Alpha by infiniteTransition.animateFloat(
        0.8f, 0f, infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Restart), label = "r1a")
    val ring2Scale by infiniteTransition.animateFloat(
        0.6f, 2f, infiniteRepeatable(tween(1200, 400, LinearEasing), RepeatMode.Restart), label = "r2s")
    val ring2Alpha by infiniteTransition.animateFloat(
        0.8f, 0f, infiniteRepeatable(tween(1200, 400, LinearEasing), RepeatMode.Restart), label = "r2a")
    val ring3Scale by infiniteTransition.animateFloat(
        0.6f, 2f, infiniteRepeatable(tween(1200, 800, LinearEasing), RepeatMode.Restart), label = "r3s")
    val ring3Alpha by infiniteTransition.animateFloat(
        0.8f, 0f, infiniteRepeatable(tween(1200, 800, LinearEasing), RepeatMode.Restart), label = "r3a")

    Box(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // Halkalar
        if (isScanning) {
            listOf(ring1Scale to ring1Alpha, ring2Scale to ring2Alpha, ring3Scale to ring3Alpha)
                .forEach { (scale, alpha) ->
                    Box(
                        modifier = Modifier.size(200.dp).scale(scale)
                            .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = alpha), CircleShape)
                    )
                }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(40.dp))

            // Başlık
            Text(
                text = if (isScanning) "Taranıyor..." else "Ders Yayını Ara",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(48.dp))

            // Ana buton
            val btnScale by animateFloatAsState(
                targetValue = if (isScanning) 1.1f else 1f,
                animationSpec = tween(500), label = "btn"
            )
            Button(
                onClick = {
                    if (isScanning) {
                        context.stopService(Intent(context, BleScannerService::class.java))
                        isScanning = false
                    } else {
                        val allGranted = permissions.all {
                            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
                        }
                        if (allGranted) {
                            startBleService(context, viewModel, isScanning = true)
                            isScanning = true
                        } else {
                            permLauncher.launch(permissions)
                        }
                    }
                },
                modifier = Modifier.size(180.dp).scale(btnScale),
                shape = RoundedCornerShape(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isScanning)
                        MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surface
                ),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Icon(
                    imageVector = if (isScanning) Icons.Filled.Bluetooth else Icons.Filled.BluetoothDisabled,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = if (isScanning) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(Modifier.height(32.dp))

            // Durum göstergesi
            lastCheckinId?.let { cid ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (reportSuccess == true)
                            Color(0xFF1B5E20) else Color(0xFF4E0D12)
                    ),
                    modifier = Modifier.padding(horizontal = 32.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.CheckCircle, null,
                            tint = if (reportSuccess == true) Color(0xFF4CAF50) else Color(0xFFCF6679))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (reportSuccess == true) "Yoklamaya katıldın! ✓" else "Bağlantı hatası, tekrar dene",
                            color = Color.White, fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Alt açıklama
        Text(
            text = if (isScanning) "Öğretmen yakındaysa otomatik kaydolacaksın"
                   else "Yoklamaya katılmak için başlat",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 60.dp)
        )
    }
}

private fun startBleService(context: Context, viewModel: HomeViewModel, isScanning: Boolean) {
    val studentId = viewModel.currentUserId ?: return
    val intent = Intent(context, BleScannerService::class.java).apply {
        putExtra(BleScannerService.EXTRA_STUDENT_ID, studentId)
    }
    context.startForegroundService(intent)
}