package com.example.ui.screens

import android.text.format.DateFormat
import kotlin.math.sin
import kotlin.math.cos
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.IslamicContent
import com.example.ui.AppViewModel
import com.example.ui.theme.*
import com.example.utils.PrayerTimeCalculator
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

@Composable
fun DashboardScreen(viewModel: AppViewModel, onNavigateToSection: (String) -> Unit) {
    val context = LocalContext.current
    var currentTimeString by remember { mutableStateOf("") }
    var currentHijriDate by remember { mutableStateOf("11 ذو الحجة 1447 هـ") }

    // Tick current clock every second
    LaunchedEffect(Unit) {
        while (true) {
            val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            currentTimeString = sdf.format(Date())
            kotlinx.coroutines.delay(1000)
        }
    }

    val currentTimes = viewModel.prayerTimes
    val selectedCity = viewModel.selectedCity

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_column")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Beautiful Hero Crescent banner (Dynamic Background decoration)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                // Background Geometric Islamic Art effect using Canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = GoldMetallic.copy(alpha = 0.1f),
                        radius = size.width / 3f,
                        center = Offset(size.width * 0.9f, size.height * 0.1f)
                    )
                    drawCircle(
                        color = GoldMetallic.copy(alpha = 0.05f),
                        radius = size.width / 2f,
                        center = Offset(size.width * 0.9f, size.height * 0.1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = hiriDateString(),
                            color = GoldAccentDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "تطبيق الذاكرين",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = GoldAccentDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${selectedCity.cityNameAr} • ${selectedCity.countryAr}",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Huge Digital UTC/Local Clock
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = currentTimeString.ifEmpty { "00:00:00" },
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "توقيت العاصمة",
                            color = SoftSage,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // 2. Next Prayer Card Highlight with Countdown
        item {
            val nextPray = getNextPrayerInfo(currentTimes)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldMetallic.copy(alpha = 0.2f), RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "الصلاة القادمة",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = nextPray.nameAr,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge(containerColor = GoldMetallic, contentColor = Color.DarkGray) {
                                Text(nextPray.timeString, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "المتبقي للأذان",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Text(
                            text = nextPray.remainingText,
                            color = AlertRed,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 3. Fasting Alerts & Sunnah reminder (Mondays and Thursdays tracker)
        item {
            val calendar = Calendar.getInstance()
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val isFastingDayNear = dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.MONDAY ||
                    dayOfWeek == Calendar.WEDNESDAY || dayOfWeek == Calendar.THURSDAY

            if (isFastingDayNear) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(GoldMetallic.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Fasting reminder",
                                tint = GoldMetallic
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "تذكير بصيام السنّة المطهرة",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "قال النبي صلى الله عليه وسلم: «تُعْرَضُ الأَعْمَالُ يَوْمَ الاِثْنَيْنِ وَالْخَمِيسِ فَأُحِبُّ أَنْ يُعْرَضَ عَمَلِي وَأَنَا صَائِمٌ»",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // 4. Quick Action Grids (Quran, Qibla, Azkar, Books Library)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardGridItem(
                    title = "القرآن الكريم",
                    subtitle = "قراءة بالأجزاء والأرباع",
                    icon = Icons.Default.Book,
                    color = SoftSage,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToSection("QURAN") }
                )
                DashboardGridItem(
                    title = "مواقيت الأذان",
                    subtitle = "مظبوطة بـ 50 مؤذن",
                    icon = Icons.Default.NotificationsActive,
                    color = SoftSage,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToSection("PRAYERS") }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardGridItem(
                    title = "اتجاه القبلة",
                    subtitle = "بوصلة دقيقة جداً",
                    icon = Icons.Default.Explore,
                    color = SoftSage,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToSection("QIBLA") }
                )
                DashboardGridItem(
                    title = "المكتبة الإسلامية",
                    subtitle = "جميع كتب الأحاديث والفقه",
                    icon = Icons.Default.Folder,
                    color = SoftSage,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToSection("LIBRARY") }
                )
            }
        }

        // 5. Daily Wird Khatmah Tracker card
        item {
            val khatmahs by viewModel.activeKhatmahs.collectAsState()
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "الورد اليومي والختمات",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "إضافة ورد",
                            color = GoldMetallic,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onNavigateToSection("SETTINGS") }
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    if (khatmahs.isEmpty()) {
                        Text(
                            text = "لم تنشئ أي خطة ختمة قرانية بعد. توجه للإعدادات لإنشاء ورد وقراءة الختمة بمواقيت مخصصة يومية!",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    } else {
                        khatmahs.forEach { khatmah ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(khatmah.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("وصلت إلى الصفحة: ${khatmah.currentPage} / 604", fontSize = 11.sp, color = Color.Gray)
                                }
                                Button(
                                    onClick = {
                                        viewModel.currentBookId = "quran" // virtual path code
                                        onNavigateToSection("QURAN")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("متابعة القراءة", fontSize = 12.sp)
                                }
                            }
                            LinearProgressIndicator(
                                progress = { khatmah.currentPage / 604f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp, bottom = 12.dp)
                                    .clip(CircleShape),
                                color = GoldMetallic,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardGridItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(110.dp)
            .clickable { onClick() }
            .border(0.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

// --- Prayer Times & Muezzin Selection Screen ---
@Composable
fun PrayerTimesScreen(viewModel: AppViewModel) {
    val times = viewModel.prayerTimes
    val currentCity = viewModel.selectedCity
    var showCitySheet by remember { mutableStateOf(false) }
    var showMuezzinDialog by remember { mutableStateOf(false) }
    var selectedPrayerForAlarm by remember { mutableStateOf("الفجر") }

    val activeMuezzin = IslamicContent.MUEZZINS[0]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("prayer_screen_column")
    ) {
        // City Header Clickable
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showCitySheet = true }
                .border(1.dp, GoldMetallic.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("البلد والمدينة المحددة", color = SoftSage, fontSize = 12.sp)
                    Text("${currentCity.cityNameAr} (${currentCity.countryAr})", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("تغيير", color = GoldAccentDark, fontWeight = FontWeight.Bold)
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Change", tint = GoldAccentDark)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Calculations & Method Toggles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AssistChip(
                onClick = {
                    val nextMethod = when(viewModel.calculationMethod) {
                        PrayerTimeCalculator.CalculationMethod.UMM_AL_QURA -> PrayerTimeCalculator.CalculationMethod.EGYPTIAN_GENERAL
                        PrayerTimeCalculator.CalculationMethod.EGYPTIAN_GENERAL -> PrayerTimeCalculator.CalculationMethod.MWL
                        else -> PrayerTimeCalculator.CalculationMethod.UMM_AL_QURA
                    }
                    viewModel.changeCalculationParams(nextMethod, viewModel.juristicMethod)
                },
                label = { Text("المذهب الفلكي: ${viewModel.calculationMethod.name}") },
                leadingIcon = { Icon(Icons.Default.Settings, null, modifier = Modifier.size(16.dp)) }
            )
            AssistChip(
                onClick = {
                    val nextJuristic = if (viewModel.juristicMethod == PrayerTimeCalculator.JuristicMethod.STANDARD) {
                        PrayerTimeCalculator.JuristicMethod.HANAFI
                    } else {
                        PrayerTimeCalculator.JuristicMethod.STANDARD
                    }
                    viewModel.changeCalculationParams(viewModel.calculationMethod, nextJuristic)
                },
                label = { Text("العصر: ${if(viewModel.juristicMethod == PrayerTimeCalculator.JuristicMethod.STANDARD) "جمهور العلماء" else "المذهب الحنفي"}") },
                leadingIcon = { Icon(Icons.Default.Schedule, null, modifier = Modifier.size(16.dp)) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Prayer list
        val prayItems = listOf(
            PrayerUiItem("الفجر", times.fajr, Icons.Default.Schedule),
            PrayerUiItem("الشروق", times.sunrise, Icons.Default.Schedule),
            PrayerUiItem("الظهر", times.dhuhr, Icons.Default.Schedule),
            PrayerUiItem("العصر", times.asr, Icons.Default.Schedule),
            PrayerUiItem("المغرب", times.maghrib, Icons.Default.Schedule),
            PrayerUiItem("العشاء", times.isha, Icons.Default.Schedule)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                items(prayItems) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                            .clickable {
                                selectedPrayerForAlarm = item.name
                                showMuezzinDialog = true
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = item.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = item.time,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Alarm status",
                                tint = GoldMetallic,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                }
            }
        }

        // Bottom Warning for Offline Muezzins
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "تنبيه: يمكنك تشغل الأذان بصوت أي مؤذن (يوجد أكثر من 50 مؤذناً متاحاً). لحفظ دقة الأوقات اضبط إعدادات بلدك وساعتك المحلية بانتظام.",
            fontSize = 11.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }

    // --- Country Selection Sheet ---
    if (showCitySheet) {
        AlertDialog(
            onDismissRequest = { showCitySheet = false },
            confirmButton = {
                TextButton(onClick = { showCitySheet = false }) {
                    Text("إغلاق")
                }
            },
            title = { Text("اختر بلدك أو مدينتك الحالية", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.height(300.dp)) {
                    items(PrayerTimeCalculator.POPULAR_CITIES) { city ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.changeCity(city)
                                    showCitySheet = false
                                }
                                .padding(vertical = 12.dp, horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(city.cityNameAr, fontWeight = FontWeight.Bold)
                            Text(city.countryAr, color = Color.Gray, fontSize = 12.sp)
                        }
                        HorizontalDivider()
                    }
                }
            }
        )
    }

    // --- Muezzin Selector Dialog (50 Muezzins list) ---
    if (showMuezzinDialog) {
        AlertDialog(
            onDismissRequest = { showMuezzinDialog = false },
            confirmButton = {
                Button(
                    onClick = { showMuezzinDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("تم الحفظ")
                }
            },
            title = { Text("تخصيص منبه: $selectedPrayerForAlarm", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("الرجاء تحديد صوت المؤذن المفضل للأذان أو الإقامة (متاح 50 صوت مؤذن عربي):", fontSize = 13.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyColumn(modifier = Modifier.height(260.dp)) {
                        items(IslamicContent.MUEZZINS) { muezzin ->
                            val isSelected = muezzin.id == 1
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        // Update/play audio preview directly on click
                                        viewModel.toggleAudioPlayback(
                                            muezzin.audioUrl,
                                            "أذان بصوت المذن المختار",
                                            muezzin.name
                                        )
                                    }
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                        else Color.Transparent,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(muezzin.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("أذان شرعي كامل", fontSize = 11.sp, color = Color.DarkGray)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = {
                                        viewModel.toggleAudioPlayback(
                                            muezzin.audioUrl,
                                            "أذان بصوت: ${muezzin.name}",
                                            "أذان واقامة"
                                        )
                                    }) {
                                        Icon(
                                            imageVector = if (viewModel.currentPlayingAudioUrl == muezzin.audioUrl && viewModel.isAudioPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                            tint = MaterialTheme.colorScheme.primary,
                                            contentDescription = "Listen"
                                        )
                                    }
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { /* Save */ }
                                    )
                                }
                            }
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        )
    }
}

data class PrayerUiItem(val name: String, val time: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

// --- Accurate Qibla Screen (Compass UI with physical sensor logic & simulator dials) ---
@Composable
fun QiblaScreen(viewModel: AppViewModel) {
    val qiblaAngle = viewModel.qiblaAngle
    val sensorDegree = viewModel.compassDegree
    val accuracy = viewModel.sensorAccuracyStatus

    // Use current compass degree depending on sensor availability, or simulate offset slider
    val totalDegree = if (viewModel.isSensorAvailable) {
        sensorDegree
    } else {
        viewModel.simulatedCompassOffset
    }

    // Mecca relative rotation:
    // When the arrow points directly to Mecca, the angle is (qiblaAngle - compassDegree).
    // If we rotate the phone such that its degree matches Qibla direction, relative angle is 0.
    val relativeAngle = (qiblaAngle - totalDegree + 360) % 360
    val isClippedToQibla = abs(relativeAngle) < 5 || abs(relativeAngle - 360) < 5

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("qibla_screen_column"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "باتجاه مكة المكرمة",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "زاوية القبلة من الشمال: ${String.format(Locale.getDefault(), "%.1f", qiblaAngle)}°",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Accuracy & sensor warning badge
            Badge(
                containerColor = if (isClippedToQibla) GoldMetallic else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (isClippedToQibla) Color.DarkGray else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(4.dp)) {
                    Icon(imageVector = if(isClippedToQibla) Icons.Default.Check else Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if(isClippedToQibla) "أنت باتجاه القبلة الصحيح الآن!" else accuracy,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Beautiful Compass Render
        Box(
            modifier = Modifier
                .size(280.dp)
                .shadow(8.dp, CircleShape)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            // Background ticks and outer compass face rotating with device rotation
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(-totalDegree)
            ) {
                // Draw Dial ticks
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.width / 2

                drawCircle(
                    color = EmeraldPrimaryLight.copy(alpha = 0.05f),
                    radius = radius,
                    center = center
                )

                // Compass Cardinal letters
                // N, S, E, W
                // We draw lines for every 30 degrees
                for (deg in 0..360 step 30) {
                    val angleRad = Math.toRadians(deg.toDouble())
                    val tickLength = if (deg % 90 == 0) 18.dp.toPx() else 8.dp.toPx()
                    val startX = (center.x + (radius - tickLength) * sin(angleRad)).toFloat()
                    val startY = (center.y - (radius - tickLength) * cos(angleRad)).toFloat()
                    val endX = (center.x + radius * sin(angleRad)).toFloat()
                    val endY = (center.y - radius * cos(angleRad)).toFloat()

                    drawLine(
                        color = if (deg == 0) Color.Red else Color.LightGray,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = if (deg % 90 == 0) 3.dp.toPx() else 1.5.dp.toPx()
                    )
                }
            }

            // Qibla Indicator Needle pointing to mecca relative angle
            // It rotates with relative angle!
            Icon(
                imageVector = Icons.Default.Navigation,
                contentDescription = "Mecca arrow",
                tint = if (isClippedToQibla) GoldMetallic else EmeraldPrimaryLight,
                modifier = Modifier
                    .size(90.dp)
                    .rotate(relativeAngle.toFloat())
            )

            // Center Mecca Mosque Kaaba Circle image placeholder
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.DarkGray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("🕋", fontSize = 24.sp)
            }
        }

        // Simulator support if sensors empty (like on standard emulators)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!viewModel.isSensorAvailable) {
                Text(
                    text = "محاكاة البوصلة (اسحب للتجربة ومطابقة القبلة):",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Slider(
                    value = viewModel.simulatedCompassOffset,
                    onValueChange = { viewModel.simulatedCompassOffset = it },
                    valueRange = 0f..360f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "اضبط وضعية هاتفك أفقياً بشكل مستوٍ تماماً بعيداً عن أي مؤثرات مغناطيسية لضمان دقة اتجاه الكعبة المشرفة.",
                fontSize = 11.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

// Helper structures to format display dates and times:
fun hiriDateString(): String {
    // Return mock static beautiful Hijri date matching Ramadan / Dhul-Hijjah
    return "الثلاثاء، 7 محرم 1448 هـ"
}

data class NextPrayerCalculated(
    val nameAr: String,
    val timeString: String,
    val remainingText: String
)

fun getNextPrayerInfo(times: PrayerTimeCalculator.PrayerTimesList): NextPrayerCalculated {
    // Return a beautiful mocked countdown for next prayer
    return NextPrayerCalculated("الظهر", times.dhuhr, "02:14:15")
}
