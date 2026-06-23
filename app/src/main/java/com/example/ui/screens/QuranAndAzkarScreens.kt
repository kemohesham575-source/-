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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.IslamicContent
import com.example.ui.AppViewModel
import com.example.ui.theme.AlertRed
import com.example.ui.theme.GoldAccentLight
import com.example.ui.theme.GoldMetallic
import com.example.ui.theme.SoftSage

@Composable
fun QuranAndAzkarTabScreen(viewModel: AppViewModel) {
    var activeSubTab by remember { mutableStateOf("QURAN") } // QURAN, AZKAR, TAJWEED, MEMORIZATION

    Column(modifier = Modifier.fillMaxSize()) {
        // Aesthetic Sub Tab Row
        TabRow(
            selectedTabIndex = when(activeSubTab) {
                "QURAN" -> 0
                "AZKAR" -> 1
                "TAJWEED" -> 2
                else -> 3
            },
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = activeSubTab == "QURAN",
                onClick = { activeSubTab = "QURAN" },
                text = { Text("المصحف الشريف", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
            )
            Tab(
                selected = activeSubTab == "AZKAR",
                onClick = { activeSubTab = "AZKAR" },
                text = { Text("الأذكار اليومية", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
            )
            Tab(
                selected = activeSubTab == "TAJWEED",
                onClick = { activeSubTab = "TAJWEED" },
                text = { Text("التجويد الميسر", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
            )
            Tab(
                selected = activeSubTab == "MEMORIZATION",
                onClick = { activeSubTab = "MEMORIZATION" },
                text = { Text("تحفيظ القرآن", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            when(activeSubTab) {
                "QURAN" -> QuranBrowserScreen(viewModel)
                "AZKAR" -> AzkarBrowserScreen(viewModel)
                "TAJWEED" -> TajweedGuideScreen()
                "MEMORIZATION" -> MemorizationHelperScreen(viewModel)
            }
        }
    }
}

// --- 1. Quran Browser & Reading Panel ---
@Composable
fun QuranBrowserScreen(viewModel: AppViewModel) {
    var selectedSurahId by remember { mutableStateOf<Int?>(null) }
    var readingTextSize by remember { mutableStateOf(19f) }
    var showTafsirBySettings by remember { mutableStateOf(true) }
    var quranListTab by remember { mutableStateOf("SURAHS") } // SURAHS, JUZZ_PARTS

    if (selectedSurahId != null) {
        val surahId = selectedSurahId!!
        val info = IslamicContent.SURAHS.find { it.id == surahId } ?: IslamicContent.SURAHS[0]
        val verses = IslamicContent.OFFLINE_VERSES[surahId] ?: listOf(
            IslamicContent.Verse(1, "عذراً، محتوى السورة كامل غير محمل افتراضياً لتوفير حجم التطبيق السريع. يمكنك تشغيل الاستماع الصوتي فوراً أو الاتصال بالإنترنت.", "طريقة الاستماع: اضغط أيقونة التشغيل بالأسفل")
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header for active surah reading
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { selectedSurahId = null }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "سورة ${info.nameAr}",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${info.type} • ${info.versesCount} آيات • الجزء ${info.juzz}",
                            color = SoftSage,
                            fontSize = 11.sp
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { readingTextSize = (readingTextSize + 2).coerceAtMost(32f) }) {
                            Icon(imageVector = Icons.Default.ZoomIn, contentDescription = "Zoom in", tint = Color.White)
                        }
                        IconButton(onClick = { readingTextSize = (readingTextSize - 2).coerceAtLeast(14f) }) {
                            Icon(imageVector = Icons.Default.ZoomOut, contentDescription = "Zoom out", tint = Color.White)
                        }
                    }
                }
            }

            // Options: Show/Hide Tafsir & Select Reciter audio
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = showTafsirBySettings,
                        onCheckedChange = { showTafsirBySettings = it }
                    )
                    Text("عرض التفسير تحت الآيات", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }

                // Reciters audio trigger for current Surah
                var showAudioDialog by remember { mutableStateOf(false) }
                Button(
                    onClick = { showAudioDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(imageVector = Icons.Default.Headphones, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("استماع صوتي", fontSize = 12.sp)
                }

                if (showAudioDialog) {
                    AlertDialog(
                        onDismissRequest = { showAudioDialog = false },
                        confirmButton = {
                            Button(onClick = { showAudioDialog = false }) {
                                Text("إغلاق")
                            }
                        },
                        title = { Text("اختر القارئ (100 قارئ متاح)", fontWeight = FontWeight.Bold) },
                        text = {
                            LazyColumn(modifier = Modifier.height(300.dp)) {
                                items(IslamicContent.RECITERS) { reciter ->
                                    val key = "reciter_${reciter.id}_surah_$surahId"
                                    val isDownloaded = viewModel.downloadTasksProgress[key] == null

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp)
                                            .clickable {
                                                // Trigger download or streaming directly
                                                viewModel.toggleAudioPlayback(
                                                    reciter.audioUrl,
                                                    "تلاوة سورة ${info.nameAr}",
                                                    reciter.nameAr
                                                )
                                                showAudioDialog = false
                                            },
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(reciter.nameAr, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text("برواية حفص عن عاصم", fontSize = 11.sp, color = Color.Gray)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(onClick = {
                                                viewModel.clickDownloadItem(key, "سورة ${info.nameAr}", reciter.nameAr)
                                            }) {
                                                val progress = viewModel.downloadTasksProgress[key]
                                                if (progress != null) {
                                                    CircularProgressIndicator(progress = progress, modifier = Modifier.size(20.dp))
                                                } else {
                                                    Icon(
                                                        imageVector = Icons.Default.ArrowDownward,
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        contentDescription = "Save offline"
                                                    )
                                                }
                                            }
                                            IconButton(onClick = {
                                                viewModel.toggleAudioPlayback(
                                                    reciter.audioUrl,
                                                    "تلاوة سورة ${info.nameAr}",
                                                    reciter.nameAr
                                                )
                                                showAudioDialog = false
                                            }) {
                                                Icon(
                                                    imageVector = if (viewModel.currentPlayingAudioUrl == reciter.audioUrl && viewModel.isAudioPlaying) Icons.Default.Close else Icons.Default.PlayArrow,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    contentDescription = "Play"
                                                )
                                            }
                                        }
                                    }
                                    HorizontalDivider()
                                }
                            }
                        }
                    )
                }
            }

            // The Arabic Verses
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Basmallah unless Al-Tawbah (9) or Fatihah (already has it as verse 1)
                if (surahId != 9 && surahId != 1) {
                    item {
                        Text(
                            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                            fontSize = (readingTextSize + 4).sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                items(verses) { verse ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                0.5.dp,
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(GoldMetallic.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${verse.id}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            // Quran script text with right alignment
                            Text(
                                text = verse.text,
                                fontSize = readingTextSize.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.weight(1f),
                                lineHeight = (readingTextSize * 1.5).sp
                            )
                        }

                        if (showTafsirBySettings) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = verse.tafsir,
                                fontSize = (readingTextSize - 5).sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth(),
                                lineHeight = (readingTextSize * 1.2).sp
                            )
                        }
                    }
                }
            }
        }
    } else {
        // Main Grid of Surahs & Parts indexing
        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(
                selectedTabIndex = if (quranListTab == "SURAHS") 0 else 1,
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Tab(
                    selected = quranListTab == "SURAHS",
                    onClick = { quranListTab = "SURAHS" },
                    text = { Text("فهرس السور (114 سورة)", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = quranListTab == "JUZZ_PARTS",
                    onClick = { quranListTab = "JUZZ_PARTS" },
                    text = { Text("القراءة بالأرباع والأجزاء", fontWeight = FontWeight.Bold) }
                )
            }

            if (quranListTab == "SURAHS") {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    items(IslamicContent.SURAHS) { surah ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedSurahId = surah.id }
                                .padding(vertical = 12.dp, horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("${surah.id}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text("سورة ${surah.nameAr}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("${surah.type} • ${surah.versesCount} آيات", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("صفحة ${surah.pageNum}", fontSize = 12.sp, color = GoldAccentLight)
                                Icon(Icons.Default.KeyboardArrowLeft, null, tint = Color.LightGray)
                            }
                        }
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                    }
                }
            } else {
                // Juzz parts & quarters list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    items(30) { index ->
                        val juzzNum = index + 1
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedSurahId = if(juzzNum == 1) 1 else 36 } // direct jump to offline compatible
                                .padding(vertical = 14.dp, horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("الجزء $juzzNum", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("يحتوي على: الحزب ${juzzNum * 2 - 1} والحزب ${juzzNum * 2} • 8 أرباع الحزب", fontSize = 12.sp, color = Color.Gray)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("ابدأ القراءة", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Icon(Icons.Default.PlayCircle, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            }
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

// --- 2. Azkar Browser Screen with repeat clicking counts & virtues ---
@Composable
fun AzkarBrowserScreen(viewModel: AppViewModel) {
    var selectedCategory by remember { mutableStateOf("MORNING") } // MORNING, EVENING, SLEEP
    val azkarList = IslamicContent.AZKARS_DATABASE.filter { it.category == selectedCategory }

    // local mutable tracking map for counts per item so users can tap and count offline!
    val countsMap = remember { mutableStateMapOf<Int, Int>() }
    // Reset clicks when category changes
    LaunchedEffect(selectedCategory) {
        countsMap.clear()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Categories row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { selectedCategory = "MORNING" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedCategory == "MORNING") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (selectedCategory == "MORNING") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("أذكار الصباح")
            }
            Button(
                onClick = { selectedCategory = "EVENING" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedCategory == "EVENING") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (selectedCategory == "EVENING") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("أذكار المساء")
            }
            Button(
                onClick = { selectedCategory = "SLEEP" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedCategory == "SLEEP") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (selectedCategory == "SLEEP") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("أذكار النوم")
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(azkarList) { zikr ->
                val limit = zikr.repeatCount
                val current = countsMap[zikr.id] ?: 0
                val finished = current >= limit

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (!finished) {
                                countsMap[zikr.id] = current + 1
                            }
                        }
                        .border(
                            1.dp,
                            if (finished) Color.Gray.copy(alpha = 0.3f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            RoundedCornerShape(16.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (finished) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = zikr.text,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth(),
                            lineHeight = 26.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = zikr.virtue,
                            fontSize = 11.sp,
                            color = Color.Gray,
                            lineHeight = 16.sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Progress counter
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val isFav by viewModel.getIsFavorite("ZIKR", "${zikr.id}").collectAsState()
                                IconButton(onClick = {
                                    viewModel.toggleFavorite(
                                        "ZIKR",
                                        "${zikr.id}",
                                        "أذكار $selectedCategory",
                                        "ذكر مكرر $limit مرات",
                                        zikr.text
                                    )
                                }) {
                                    Icon(
                                        imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        tint = AlertRed,
                                        contentDescription = "fav"
                                    )
                                }
                            }

                            // Counter Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (finished) Color.Gray else MaterialTheme.colorScheme.primary
                                    )
                                    .padding(horizontal = 20.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (finished) "تم القراءة ✓" else "التكرار: $current / $limit",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- 3. TajweedIllustrated Guide Screening ---
@Composable
fun TajweedGuideScreen() {
    val lessons = IslamicContent.TAJWEED_LESSONS

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "تعلم التجويد وأحكام التلاوة الصحيحة",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "منهج كامل مصور مع شرح مفصل لمخارج الحروف، الإظهار والادغام والمدود لتجويد كلام رب العالمين.",
                        color = SoftSage,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        items(lessons) { lesson ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "درس: ${lesson.title}",
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = lesson.detailText,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// --- 4. Quran Memorization panel (Hide and Reveal verse aid) ---
@Composable
fun MemorizationHelperScreen(viewModel: AppViewModel) {
    var selectedSurahId by remember { mutableStateOf(112) } // default Ikhlas
    var isVerseHidden by remember { mutableStateOf(false) }
    var selectedVerseIndex by remember { mutableStateOf(0) }

    val activeSurahInQuran = IslamicContent.SURAHS.find { it.id == selectedSurahId } ?: IslamicContent.SURAHS[0]
    val verses = IslamicContent.OFFLINE_VERSES[selectedSurahId] ?: listOf(
        IslamicContent.Verse(1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "Tafsir content")
    )
    val currentVerse = verses.getOrNull(selectedVerseIndex) ?: verses[0]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("المساعد التفاعلي لحفظ وتثبيت القرآن", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
            Text("اختر الآية، شغل التكرار، وأخفِ النص لاختبار حفظك الغيبي بكل سهولة.", fontSize = 12.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            // Selection Options Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        selectedSurahId = if (selectedSurahId == 112) 113 else 112
                        selectedVerseIndex = 0
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("السورة: ${activeSurahInQuran.nameAr}")
                }
                Button(
                    onClick = {
                        selectedVerseIndex = (selectedVerseIndex + 1) % verses.size
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("الآية رقم: ${currentVerse.id}")
                }
            }
        }

        // Display Verse card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(vertical = 12.dp)
                .border(2.dp, GoldMetallic.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isVerseHidden) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("النص مخفي للحفظ الغيبي 🔒", color = Color.Gray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("اقرأ الآن غيباً، ثم اضغط كشف للتحقق", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Text(
                        currentVerse.text,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Controls Area
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { isVerseHidden = !isVerseHidden },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isVerseHidden) MaterialTheme.colorScheme.primary else GoldMetallic,
                    contentColor = if (isVerseHidden) Color.White else Color.DarkGray
                ),
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Icon(
                    imageVector = if (isVerseHidden) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isVerseHidden) "كشف نص الآية" else "إخفاء النص للحفظ وتسميع")
            }

            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.toggleAudioPlayback(
                            "http://server.example.com/verse_${selectedSurahId}_${currentVerse.id}.mp3",
                            "تكرار تلاوة الآية",
                            "الآية ${currentVerse.id} من سورة ${activeSurahInQuran.nameAr}"
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PlayArrow, null)
                    Text("تشغيل الغناء")
                }

                Button(
                    onClick = {
                        selectedVerseIndex = (selectedVerseIndex + 1) % verses.size
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("الآية التالية")
                    Icon(Icons.Default.ArrowForward, null)
                }
            }
        }
    }
}
