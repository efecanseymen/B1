package com.efecanseymen.b1.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.efecanseymen.b1.viewmodel.HomeViewModel

@Composable
fun ClassScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val nfcData      by viewModel.nfcTagData.observeAsState()
    val nfcAvailable by viewModel.nfcAvailable.observeAsState(false)

    // Halka animasyonu (NFC bekleme)
    val infiniteTransition = rememberInfiniteTransition(label = "nfc_rings")
    val ring1Scale by infiniteTransition.animateFloat(
        0.7f, 2f,
        infiniteRepeatable(tween(1400, easing = LinearEasing), RepeatMode.Restart),
        label = "r1"
    )
    val ring1Alpha by infiniteTransition.animateFloat(
        0.6f, 0f,
        infiniteRepeatable(tween(1400, easing = LinearEasing), RepeatMode.Restart),
        label = "r1a"
    )
    val ring2Scale by infiniteTransition.animateFloat(
        0.7f, 2f,
        infiniteRepeatable(tween(1400, 500, LinearEasing), RepeatMode.Restart),
        label = "r2"
    )
    val ring2Alpha by infiniteTransition.animateFloat(
        0.6f, 0f,
        infiniteRepeatable(tween(1400, 500, LinearEasing), RepeatMode.Restart),
        label = "r2a"
    )

    // İkon pulse (kart okunduğunda)
    val successScale by animateFloatAsState(
        targetValue = if (nfcData != null) 1.15f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "success"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {

            // Başlık
            Text(
                text = "Hangi Derslik?",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (nfcAvailable) "NFC kartını cihaza yaklaştır"
                       else "Bu cihazda NFC desteklenmiyor",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(40.dp))

            // NFC ikon + halkalar
            Box(contentAlignment = Alignment.Center) {
                if (nfcData == null && nfcAvailable) {
                    // Yayılan halkalar
                    listOf(ring1Scale to ring1Alpha, ring2Scale to ring2Alpha).forEach { (scale, alpha) ->
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .scale(scale)
                                .border(
                                    2.dp,
                                    MaterialTheme.colorScheme.primary.copy(alpha = alpha),
                                    CircleShape
                                )
                        )
                    }
                }

                // Merkez ikon dairesi
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(successScale)
                        .background(
                            color = if (nfcData != null)
                                Color(0xFF4CAF50).copy(alpha = 0.15f)
                            else
                                MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (nfcData != null) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = Color(0xFF4CAF50)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Nfc,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = if (nfcAvailable)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(36.dp))

            // Kart okundu → sonuç göster
            AnimatedVisibility(
                visible = nfcData != null,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut()
            ) {
                nfcData?.let { data ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "📍 Derslik Bilgisi",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = data,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = { viewModel.clearNfcTag() },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Temizle")
                    }
                }
            }

            // Bekleme metni (kart okunmadıysa)
            AnimatedVisibility(visible = nfcData == null && nfcAvailable) {
                Text(
                    text = "Kart bekleniyor...",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}