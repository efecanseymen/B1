package com.efecanseymen.b1.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.efecanseymen.b1.viewmodel.HomeViewModel

@Composable
fun ScanScreen(viewModel: HomeViewModel, modifier: Modifier) {
    var isScanning by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isScanning) 1.2f else 1f,
        animationSpec = tween(500),
        label = "buttonScale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "rings")

    val ring1Scale by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Restart),
        label = "ring1scale"
    )
    val ring1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Restart),
        label = "ring1alpha"
    )
    val ring2Scale by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(1200, delayMillis = 400, easing = LinearEasing), RepeatMode.Restart),
        label = "ring2scale"
    )
    val ring2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(1200, delayMillis = 400, easing = LinearEasing), RepeatMode.Restart),
        label = "ring2alpha"
    )
    val ring3Scale by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(1200, delayMillis = 800, easing = LinearEasing), RepeatMode.Restart),
        label = "ring3scale"
    )
    val ring3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(1200, delayMillis = 800, easing = LinearEasing), RepeatMode.Restart),
        label = "ring3alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // Animasyonlu halkalar
        if (isScanning) {
            listOf(
                ring1Scale to ring1Alpha,
                ring2Scale to ring2Alpha,
                ring3Scale to ring3Alpha
            ).forEach { (scale, alpha) ->
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .scale(scale)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = alpha),
                            shape = CircleShape
                        )
                )
            }
        }

        AnimatedVisibility(
            visible = !isScanning,
            enter = fadeIn(animationSpec = tween(500)),
            exit = fadeOut(animationSpec = tween(500))
        ) {
            Text(
                text = "Taramayı Başlat",
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .offset(y = -100.dp)
            )
        }

        // Ana buton
        Button(
            onClick = { isScanning = !isScanning },
            modifier = Modifier
                .size(180.dp)
                .scale(scale),
            shape = RoundedCornerShape(40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Nfc,
                contentDescription = "NFC",
                modifier = Modifier.size(120.dp),
                tint = if (isScanning) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        // Alt yazı
        Text(
            text = if (isScanning) "Taranıyor..." else "Okutmak için yaklaştır",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
        )
    }
}