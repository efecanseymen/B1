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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.efecanseymen.b1.data.model.StudentAttendanceItem
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
        else      -> Color(0xFFCF6679)
    }

    // Günleri tarihe göre sırala
    val sortedAttendance = course.attendance?.sortedBy { it.date }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // ── Üst satır: renkli çizgi + bilgi + yüzde ──
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(if (sortedAttendance.isNullOrEmpty()) 72.dp else 72.dp)
                        .background(barColor, RoundedCornerShape(topStart = 14.dp, bottomStart = if (sortedAttendance.isNullOrEmpty()) 14.dp else 0.dp))
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

            // ── Gün-Gün Katılım Bandı ──
            if (!sortedAttendance.isNullOrEmpty()) {
                val recentAttendance = sortedAttendance.takeLast(10)
                val startIndex = maxOf(0, sortedAttendance.size - 10)

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        "Gün-Gün Katılım (Son ${recentAttendance.size} Ders)",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(6.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        recentAttendance.forEachIndexed { i, day ->
                            StudentDayChip(
                                dayIndex = startIndex + i + 1,
                                item     = day,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Sıranın geri kalanını boş doldur
                        repeat((10 - recentAttendance.size).coerceAtLeast(0)) {
                            Box(modifier = Modifier.weight(1f))
                        }
                    }

                    Spacer(Modifier.height(4.dp))
                    // Açıklama
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StudentLegendItem(Color(0xFF4CAF50), "Katıldı")
                        StudentLegendItem(Color(0xFFCF6679), "Katılmadı")
                    }
                }
            }
        }
    }
}

@Composable
fun StudentDayChip(dayIndex: Int, item: StudentAttendanceItem, modifier: Modifier = Modifier) {
    val isPresent = item.status == "present"
    val color = if (isPresent) Color(0xFF4CAF50) else Color(0xFFCF6679)
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(color.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isPresent) "✓" else "✗",
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
        Text(
            text = "$dayIndex",
            fontSize = 8.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun StudentLegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Spacer(Modifier.width(4.dp))
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
