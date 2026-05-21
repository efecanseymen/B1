package com.efecanseymen.b1.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.efecanseymen.b1.R
import com.efecanseymen.b1.viewmodel.HomeViewModel

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val loginResult by viewModel.loginResult.observeAsState()
    val errorMessage by viewModel.errorMessage.observeAsState()

    val animatedBorderColor by animateColorAsState(
        targetValue = when {
            loginResult?.success == false -> Color(0xFFCF6679)
            loginResult?.success == true -> Color(0xFF4CAF50)
            else -> MaterialTheme.colorScheme.outline
        },
        animationSpec = tween(300),
        label = "borderColor"
    )

    val animatedLabelColor by animateColorAsState(
        targetValue = when {
            loginResult?.success == false -> Color(0xFFCF6679)
            loginResult?.success == true -> Color(0xFF4CAF50)
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(300),
        label = "labelColor"
    )

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = animatedBorderColor,
        unfocusedBorderColor = animatedBorderColor,
        focusedLabelColor = animatedLabelColor,
        unfocusedLabelColor = animatedLabelColor
    )

    LaunchedEffect(loginResult) {
        if (loginResult?.success == true) {
            onLoginClick()
        }
        else if(loginResult?.success == false){
            Toast.makeText(context, "Giriş başarısız! Lütfen bilgilerinizi kontrol edin.", Toast.LENGTH_SHORT).show()

        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.au_logo),
            contentDescription = null,
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                if (loginResult?.success == false) viewModel.clearLoginResult()
            },
            label = { Text("Öğrenci Numarası") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = textFieldColors
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                if (loginResult?.success == false) viewModel.clearLoginResult()
            },
            label = { Text("Şifre") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            colors = textFieldColors
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (loginResult?.success == false) {
            Text(
                text = "Giriş başarısız! Lütfen bilgilerinizi kontrol edin.",
                color = Color(0xFFCF6679),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.clearLoginResult()
                viewModel.login(username, password)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Giriş Yap", fontSize = 16.sp)
        }
    }
}