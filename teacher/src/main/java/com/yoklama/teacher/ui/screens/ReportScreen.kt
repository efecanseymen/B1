package com.yoklama.teacher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoklama.teacher.viewmodel.TeacherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    viewModel: TeacherViewModel,
    courseCode: String,
    onBack: () -> Unit
) {
    val report by viewModel.sessionReport.observeAsState()

    LaunchedEffect(courseCode) {
        viewModel.loadReport(courseCode)
    }

    var threshold by remember { mutableStateOf("70") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rapor — $courseCode", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Eşik belirle
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Devamsızlık Eşiği (%)", fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = threshold,
                            onValueChange = { threshold = it.filter { c -> c.isDigit() }.take(3) },
                            modifier = Modifier.width(100.dp),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            suffix = { Text("%") }
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Bu eşiğin altındaki öğrenciler devamsızlıktan kalır.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            report?.let { r ->
                Text("${r.total_sessions} ders tamamlandı",
                    fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))

                val thresholdVal = threshold.toIntOrNull() ?: 70
                val students = r.students ?: emptyList()

                val passing = students.filter { it.percentage >= thresholdVal }
                val failing = students.filter { it.percentage < thresholdVal }

                if (failing.isNotEmpty()) {
                    Text("❌ Devamsızlıktan Kalan (${failing.size} kişi)",
                        fontWeight = FontWeight.Bold, color = Color(0xFFCF6679))
                    Spacer(Modifier.height(6.dp))
                }

                LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(failing) { s ->
                        ReportCard(s.student_name, s.present_sessions, r.total_sessions ?: 0,
                            s.percentage, isPassing = false)
                    }
                    if (passing.isNotEmpty()) {
                        item {
                            Spacer(Modifier.height(8.dp))
                            Text("✅ Devam Eden (${passing.size} kişi)",
                                fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                            Spacer(Modifier.height(6.dp))
                        }
                    }
                    items(passing) { s ->
                        ReportCard(s.student_name, s.present_sessions, r.total_sessions ?: 0,
                            s.percentage, isPassing = true)
                    }
                }
            } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun ReportCard(name: String, present: Int, total: Int, pct: Double, isPassing: Boolean) {
    val color = if (isPassing) Color(0xFF4CAF50) else Color(0xFFCF6679)
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(10.dp).background(color, CircleShape))
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.SemiBold)
                Text("$present / $total ders", fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("%${pct.toInt()}",
                fontWeight = FontWeight.Bold, color = color, fontSize = 16.sp)
        }
    }
}
