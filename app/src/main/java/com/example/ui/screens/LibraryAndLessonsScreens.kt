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
import com.example.data.IslamicContent
import com.example.ui.AppViewModel
import com.example.ui.theme.GoldAccentLight
import com.example.ui.theme.GoldMetallic
import com.example.ui.theme.SoftSage

@Composable
fun LibraryAndLessonsTabScreen(viewModel: AppViewModel) {
    var activeSubTab by remember { mutableStateOf("LIBRARY") } // LIBRARY, LESSONS

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = if (activeSubTab == "LIBRARY") 0 else 1,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = activeSubTab == "LIBRARY",
                onClick = { activeSubTab = "LIBRARY" },
                text = { Text("المكتبة الإسلامية الشاملة", fontWeight = FontWeight.Bold, fontSize = 14.sp) }
            )
            Tab(
                selected = activeSubTab == "LESSONS",
                onClick = { activeSubTab = "LESSONS" },
                text = { Text("محاضرات ودروس المشايخ", fontWeight = FontWeight.Bold, fontSize = 14.sp) }
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            if (activeSubTab == "LIBRARY") {
                IslamicLibraryExplorer(viewModel)
            } else {
                LecturesLibraryExplorer(viewModel)
            }
        }
    }
}

// --- 1. Islamic Library Explorer with Native Reader Dialog ---
@Composable
fun IslamicLibraryExplorer(viewModel: AppViewModel) {
    var selectedCategory by remember { mutableStateOf<String?>("HADITH") } // HADITH, SEERAH, FIQH, IBN_ALQAYYIM, PROPHETS, KNOWLEDGE
    var activeReadingBookId by remember { mutableStateOf<String?>(null) }
    var searchBookText by remember { mutableStateOf("") }

    val categoriesAr = mapOf(
        "HADITH" to "كتب الأحاديث الستة",
        "SEERAH" to "السيرة النبوية والمحمدية",
        "FIQH" to "فقه المذاهب الأربعة",
        "IBN_ALQAYYIM" to "كتب ابن قيم الجوزية",
        "PROPHETS" to "قصص الأنبياء والقرن",
        "KNOWLEDGE" to "كتب مخصصة لطلبة العلم"
    )

    if (activeReadingBookId != null) {
        val bookId = activeReadingBookId!!
        val chapters = IslamicContent.OFFLINE_BOOK_PAGES[bookId] ?: listOf(
            IslamicContent.BookPage(1, "الباب الأول", "عذراً أخي، هذا الكتاب متوفر كفهرس بحث ودروس شرعية مخصصة. يمكنك استخدام البحث الذكي بالذكاء الاصطناعي لسؤال الشيخ عن هذا الباب وتوليده في الحال وبدقة كاملة!")
        )
        var currentBookPageNum by remember { mutableStateOf(1) }
        val page = chapters.find { it.pageNum == currentBookPageNum } ?: chapters[0]

        // Beautiful reading viewer dialog with adjustable sizes and favorites
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Reader header
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
                    IconButton(onClick = { activeReadingBookId = null }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                    Text(
                        text = page.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Book Content Display
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    item {
                        Text(
                            text = page.text,
                            fontSize = 18.sp,
                            lineHeight = 28.sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Navigation between chapters / pages
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        val prev = currentBookPageNum - 1
                        if (prev >= 1 && chapters.any { it.pageNum == prev }) {
                            currentBookPageNum = prev
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = currentBookPageNum > 1
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("السابق")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        val next = currentBookPageNum + 1
                        if (chapters.any { it.pageNum == next }) {
                            currentBookPageNum = next
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = chapters.any { it.pageNum == currentBookPageNum + 1 }
                ) {
                    Text("التالي")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Forward")
                }
            }
        }
    } else {
        // Main list of Books categorized
        Column(modifier = Modifier.fillMaxSize()) {
            // horizontal scrollable filters row
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        "تصنيفات المكتبة الإسلامية",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                // Grid selection of categories
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        categoriesAr.forEach { (catKey, catName) ->
                            val isSelected = selectedCategory == catKey
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedCategory = catKey }
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(vertical = 12.dp, horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = null,
                                        tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(catName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Icon(
                                    imageVector = if (isSelected) Icons.Default.Check else Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // Books inside selected category area
                if (selectedCategory != null) {
                    val books = IslamicContent.SECTIONS_AND_BOOKS[selectedCategory] ?: emptyList()
                    val catTitle = categoriesAr[selectedCategory] ?: ""

                    item {
                        Text(
                            "الكتب المدرجة في: $catTitle",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 8.dp)
                        )
                    }

                    items(books) { book ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { activeReadingBookId = book.id }
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(book.title, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                                    IconButton(onClick = {
                                        viewModel.toggleFavorite("BOOK", book.id, book.title, book.author, book.description)
                                    }) {
                                        val isFav by viewModel.getIsFavorite("BOOK", book.id).collectAsState()
                                        Icon(
                                            imageVector = if (isFav) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                            tint = GoldMetallic,
                                            contentDescription = "Save bookmark"
                                        )
                                    }
                                }
                                Text("المؤلف: ${book.author}", fontSize = 12.sp, color = GoldAccentLight, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(book.description, fontSize = 12.sp, color = Color.Gray, lineHeight = 18.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.primary)
                                            .padding(horizontal = 12.dp, vertical = 4.dp)
                                    ) {
                                        Text("افتح واقرأ مباشرة داخل التطبيق", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- 2. Lectures Panel with direct simulated downloader and play features ---
@Composable
fun LecturesLibraryExplorer(viewModel: AppViewModel) {
    val items = IslamicContent.LECTURES

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("lectures_column")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "دروس ومشايخ الأمة الأجلاء",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "استمع فوراً لمحاضرات وقصص وفتاوى مسجلة للشيوخ والعلماء مع إمكانية تحميلها والاستماع لها لاحقاً بدون إنترنت.",
                        color = SoftSage,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        items(items) { lecture ->
            val fileKey = "lecture_speech_${lecture.id}"
            val progress = viewModel.downloadTasksProgress[fileKey]

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = lecture.speaker,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "مثال من المحاضرات: ${lecture.latestTopic}",
                        fontSize = 12.sp,
                        color = GoldAccentLight,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = lecture.description,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Download button
                        Button(
                            onClick = {
                                viewModel.clickDownloadItem(fileKey, lecture.latestTopic, lecture.speaker)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (progress != null) Color.Gray else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            if (progress != null) {
                                CircularProgressIndicator(
                                    progress = progress,
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("جاري تنزيل ملف الدرس...", fontSize = 11.sp)
                            } else {
                                Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("تنزيل للاستماع بدون إنترنت", fontSize = 11.sp)
                            }
                        }

                        // Play Button
                        IconButton(
                            onClick = {
                                viewModel.toggleAudioPlayback(
                                    "http://example.com/lecture_speech_${lecture.id}.mp3",
                                    lecture.latestTopic,
                                    lecture.speaker
                                )
                            }
                        ) {
                            Icon(
                                imageVector = if (viewModel.currentPlayingAudioUrl == "http://example.com/lecture_speech_${lecture.id}.mp3" && viewModel.isAudioPlaying) Icons.Default.Close else Icons.Default.PlayArrow,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(38.dp),
                                contentDescription = "Play"
                            )
                        }
                    }
                }
            }
        }
    }
}
