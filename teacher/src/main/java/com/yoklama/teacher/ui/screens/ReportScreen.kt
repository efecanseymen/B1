package com.yoklama.teacher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoklama.teacher.data.model.DailyAttendance
import com.yoklama.teacher.data.model.StudentReportItem
import com.yoklama.teacher.viewmodel.TeacherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    viewModel: TeacherViewModel,
    courseCode: String,
    onBack: () -> Unit
) {
    var thresholdInput by remember { mutableStateOf("70") }
    val report by viewModel.sessionReport.observeAsState()
    var loaded by remember { mutableStateOf(false) }

    // İlk yüklemede raporu çek
    LaunchedEffect(Unit) {
        val t = thresholdInput.toDoubleOrNull() ?: 70.0
        viewModel.loadReport(courseCode, t)
        loaded = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rapor — $courseCode", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Geri")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {

            // ─── Devamsızlık Eşiği Kartı ───────────────────────────
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Devamsızlık Eşiği (%)",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = thresholdInput,
                                onValueChange = { thresholdInput = it.filter(Char::isDigit).take(3) },
                                modifier = Modifier.width(88.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                suffix = { Text("%", color = MaterialTheme.colorScheme.onPrimary) }
                            )
                            Column {
                                Text(
                                    "Bu eşiğin altındaki öğrenciler",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                                )
                                Text(
                                    "devamsızlıktan kalır.",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = {
                                val t = thresholdInput.toDoubleOrNull() ?: 70.0
                                viewModel.loadReport(courseCode, t)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.onPrimary,
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Hesapla", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // ─── Özet Bilgi ───────────────────────────────────────
            report?.let { r ->
                item {
                    val totalSessions = r.total_sessions ?: 0
                    val students = r.students ?: emptyList()
                    val passing = students.count { it.status == "passing" }
                    val failing = students.size - passing

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SummaryChip("$totalSessions Ders", MaterialTheme.colorScheme.surfaceVariant, Modifier.weight(1f))
                        SummaryChip("✅ $passing Başarılı", Color(0xFF1B5E20).copy(alpha = 0.2f), Modifier.weight(1f), Color(0xFF4CAF50))
                        SummaryChip("❌ $failing Başarısız", Color(0xFFB71C1C).copy(alpha = 0.2f), Modifier.weight(1f), Color(0xFFCF6679))
                    }
                }

                // ─── Öğrenci Kartları (gün-gün) ───────────────────
                val students = r.students ?: emptyList()

                if (students.isEmpty()) {
                    item {
                        Text(
                            "Henüz tamamlanan ders yok.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 24.dp)
                        )
                    }
                } else {
                    // Önce başarısızlar, sonra başarılılar
                    val sorted = students.sortedWith(compareBy({ it.status == "passing" }, { -it.percentage }))
                    items(sorted) { student ->
                        StudentAttendanceCard(
                            student = student,
                            threshold = thresholdInput.toDoubleOrNull() ?: 70.0
                        )
                    }
                }
            } ?: item {
                if (loaded) {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryChip(
    text: String,
    background: Color,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = textColor)
    }
}

@Composable
fun StudentAttendanceCard(student: StudentReportItem, threshold: Double) {
    val isPassing = student.status == "passing"
    val borderColor = if (isPassing) Color(0xFF4CAF50) else Color(0xFFCF6679)
    val bgColor = if (isPassing)
        Color(0xFF1B5E20).copy(alpha = 0.08f)
    else
        Color(0xFFB71C1C).copy(alpha = 0.08f)

    Card(
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(bgColor)
                .padding(14.dp)
        ) {
            // Öğrenci başlığı
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isPassing) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                    contentDescription = null,
                    tint = borderColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text(student.student_name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(student.student_id, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                // Genel yüzde
                Box(
                    modifier = Modifier
                        .background(borderColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        "%${student.percentage.toInt()}",
                        fontWeight = FontWeight.ExtraBold,
                        color = borderColor,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(Modifier.height(4.dp))
            Text(
                "${student.present_sessions}/${student.total_sessions} gün katıldı",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Gün-gün katılım bandı
            val daily = student.daily_attendance
            if (!daily.isNullOrEmpty()) {
                Spacer(Modifier.height(10.dp))
                Text("Gün-Gün Katılım:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    daily.forEachIndexed { index, day ->
                        DayChip(dayIndex = index + 1, day = day, modifier = Modifier.weight(1f))
                    }
                    // Boş kutular (maks 10 seçeneğe pad)
                    repeat((10 - daily.size).coerceAtLeast(0)) {
                        Box(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(Modifier.height(4.dp))
                // Açıklama
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    LegendItem(Color(0xFF4CAF50), "Katıldı")
                    LegendItem(Color(0xFFCF6679), "Katılmadı")
                }
            }
        }
    }
}

@Composable
fun DayChip(dayIndex: Int, day: DailyAttendance, modifier: Modifier = Modifier) {
    val isPresent = day.status == "present"
    val color = if (isPresent) Color(0xFF4CAF50) else Color(0xFFCF6679)
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isPresent) "✓" else "✗",
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
        Text(
            text = "$dayIndex",
            fontSize = 9.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Spacer(Modifier.width(4.dp))
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
