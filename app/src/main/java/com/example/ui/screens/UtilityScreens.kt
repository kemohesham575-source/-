package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppViewModel
import com.example.ui.theme.AlertRed
import com.example.ui.theme.GoldAccentLight
import com.example.ui.theme.GoldMetallic
import com.example.ui.theme.SoftSage

// --- 1. Tasbih Screen ---
@Composable
fun TasbihScreen(viewModel: AppViewModel) {
    val logs by viewModel.tasbihLogs.collectAsState()
    var selectedPhraze by remember { mutableStateOf("سُبْحَانَ اللهِ") }

    val phrases = listOf(
        "سُبْحَانَ اللهِ",
        "الْحَمْدُ للهِ",
        "لَا إِلٰهَ إِلَّا اللهُ",
        "اللهُ أَكْبَرُ",
        "أَسْتَغْفِرُ اللهَ",
        "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("tasbih_screen")
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("مسبحة الذاكرين الإلكترونية", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
            Text("اختر الذكر واضغط على العداد الكبير للبدء بالتسبيح مع التخزين التلقائي للجلسات.", fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(16.dp))

            // Phrase selection chip row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                LazyColumn(modifier = Modifier.height(110.dp).fillMaxWidth()) {
                    items(phrases.chunked(2)) { pair ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            pair.forEach { phrase ->
                                AssistChip(
                                    onClick = {
                                        viewModel.resetTasbihSession()
                                        selectedPhraze = phrase
                                        viewModel.selectedTasbihZikr = phrase
                                    },
                                    label = { Text(phrase, fontWeight = FontWeight.Bold) },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = if (selectedPhraze == phrase) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Circular Tapper Dial
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .border(6.dp, GoldMetallic.copy(alpha = 0.5f), CircleShape)
                .clickable { viewModel.incrementTasbih() },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${viewModel.tasbihCurrentCount}",
                    color = Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "اضغط للتسبيح",
                    color = SoftSage,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "الهدف: ${viewModel.tasbihTarget}",
                    color = GoldMetallic,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Quick settings and history stats log
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Target Toggle
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("الهدف:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    listOf(33, 99, 1000).forEach { target ->
                        FilterChip(
                            selected = viewModel.tasbihTarget == target,
                            onClick = { viewModel.tasbihTarget = target },
                            label = { Text("$target") },
                            modifier = Modifier.padding(horizontal = 2.dp)
                        )
                    }
                }

                TextButton(onClick = { viewModel.resetTasbihSession() }) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إعادة تصفير")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // History log
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("سجل التسبيحات اليومي", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(
                            "مسح السجل", 
                            color = AlertRed, 
                            fontSize = 11.sp, 
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { viewModel.clearAllTasbihLogs() }
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    if (logs.isEmpty()) {
                        Text("لا يوجد تسبيحات مسجلة اليوم. ابدأ بذكر الله وسيظهر سجلك هنا تلقائياً.", fontSize = 11.sp, color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(logs) { log ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(log.name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    Text("أتم العداد: ${log.finalCount} تسبيحة", fontSize = 12.sp, color = GoldAccentLight, fontWeight = FontWeight.Bold)
                                }
                                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- 2. Settings, Language & Khatmah Schedule Manager Screen ---
@Composable
fun SettingsScreen(viewModel: AppViewModel) {
    var selectedLanguage by remember { mutableStateOf("العربية (ar)") }
    var khatmahName by remember { mutableStateOf("") }
    var khatmahDays by remember { mutableStateOf("30") }

    val languages = listOf(
        "العربية (ar)",
        "English (en)",
        "Bahasa Indonesia (id)",
        "Türkçe (tr)",
        "Français (fr)",
        "اردو (ur)"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Chat Section
        item {
            Text(
                "إعدادات تطبيق الذاكرين",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Language Chooser
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("لغة التطبيق والترجمات", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    languages.forEach { language ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedLanguage = language }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(language, fontSize = 13.sp)
                            RadioButton(
                                selected = selectedLanguage == language,
                                onClick = { selectedLanguage = language }
                            )
                        }
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                    }
                }
            }
        }

        // Add Khatmah Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("إضافة خطة ختمة قرانية ذكية", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = khatmahName,
                        onValueChange = { khatmahName = it },
                        placeholder = { Text("مثال: ختمة رمضان المبارك، ختمة الجمعة...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = khatmahDays,
                        onValueChange = { khatmahDays = it },
                        placeholder = { Text("عدد الأيام المستهدفة للختمة (أرقام فقط)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val days = khatmahDays.toIntOrNull() ?: 30
                            val name = khatmahName.ifEmpty { "ختمة مخصصة جديدة" }
                            viewModel.addKhatmah(name, days)
                            khatmahName = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("إنشاء خطة تتبع الورد التلقائي")
                    }
                }
            }
        }

        // Favorites and Offline Downloads Management
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                val dbDownloads by viewModel.downloads.collectAsState()
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("إدارة البيانات المحملة والمفضلة", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "يقوم التطبيق بتحميل الملفات الصوتية للقرآن والمحاضرات لتشغيلها بالكامل بدون نت فوراً لتوفير تجربة سلسة.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (dbDownloads.isEmpty()) {
                        Text("لا يوجد دروس أو تلاوات محملة حالياً (الملفات الافتراضية تفتح أونلاين وتحمل بطلبك).", fontSize = 12.sp, color = Color.Gray)
                    } else {
                        dbDownloads.forEach { download ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(download.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("القارئ: ${download.reciterName} • متوفر بدون إنترنت ✓", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = { viewModel.clickDownloadItem(download.fileKey, download.title, download.reciterName) }) {
                                    Icon(imageVector = Icons.Default.Delete, tint = Color.Red, contentDescription = "delete")
                                }
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }
        }

        // About Al-Dhakireen
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("حول تطبيق الذاكرين", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "التطبيق الإسلامي الأقوى ببلاد المسلمين، صُمّم كعمل خالص لخدمة الأمة وبناء جيل حافِظ ذاكِر لله على الدوام.",
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
