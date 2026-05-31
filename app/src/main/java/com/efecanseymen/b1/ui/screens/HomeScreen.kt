package com.efecanseymen.b1.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.efecanseymen.b1.data.model.StudentCourseInfo
import com.efecanseymen.b1.viewmodel.HomeViewModel

data class ClassInfo(val className: String, val description: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier,
    onLogOutClick: () -> Unit
) {
    val userName     = viewModel.currentUserName ?: "Öğrenci"
    val courses      by viewModel.courses.observeAsState(emptyList())
    val isLoading    by viewModel.isLoadingCourses.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState()

    LaunchedEffect(Unit) { viewModel.loadCourses() }

    // Scaffold ile edge-to-edge düzgün çalışır, TopAppBar status bar'ın altında kalır
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Hoşgeldin ${userName.substringBefore(' ')}!",
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                actions = {
                    IconButton(onClick = onLogOutClick) {
                        Icon(
                            imageVector = Icons.Filled.Logout,
                            contentDescription = "Çıkış",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Başlık bandı
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = if (isLoading) "Yükleniyor..." else "Derslerim (${courses.size})",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            when {
                isLoading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }

                courses.isEmpty() -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.School, null,
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Kayıtlı ders bulunamadı",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        errorMessage?.let {
                            Spacer(Modifier.height(4.dp))
                            Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        }
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(onClick = { viewModel.loadCourses() }) {
                            Text("Tekrar Dene")
                        }
                    }
                }

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)
                ) {
                    items(courses) { course -> CourseListItem(course) }
                }
            }
        }
    }
}

@Composable
fun CourseListItem(course: StudentCourseInfo) {
    val pct = course.overall_percentage
    val barColor = when {
        course.total_sessions == 0 -> MaterialTheme.colorScheme.outline
        pct >= 80 -> Color(0xFF4CAF50)
        pct >= 60 -> Color(0xFFFFC107)
        else -> Color(0xFFCF6679)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(80.dp)
                    .background(barColor, RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .weight(1f)
            ) {
                Text(course.course_name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    course.course_code, fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                val statusText = if (course.total_sessions == 0)
                    "Henüz ders yok"
                else
                    "${course.present_sessions}/${course.total_sessions} ders — %${pct.toInt()} devamlılık"
                Text(statusText, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (course.total_sessions > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 14.dp)
                        .background(barColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        "%${pct.toInt()}",
                        fontWeight = FontWeight.ExtraBold,
                        color = barColor,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
