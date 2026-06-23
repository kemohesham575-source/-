package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppViewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import androidx.compose.ui.platform.testTag
import com.example.ui.theme.GoldMetallic

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Ensure layout direction is strictly RightToLeft for gorgeous Arabic native reading alignment!
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val viewModel: AppViewModel = viewModel()
                    var currentTab by remember { mutableStateOf("DASHBOARD") }

                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("main_app_scaffold"),
                        bottomBar = {
                            Column {
                                // 1. Custom Floating Audio Player Widget if music plays
                                if (viewModel.isAudioPlaying) {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                            .testTag("audio_player_panel"),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1f)
                                                    .padding(end = 8.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.MusicNote,
                                                    contentDescription = "Playing",
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(
                                                        viewModel.audioTitle,
                                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                                        fontSize = 13.sp,
                                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                                    )
                                                    Text(
                                                        viewModel.audioSubtitle,
                                                        fontSize = 11.sp,
                                                        color = Color.Gray
                                                    )
                                                }
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                IconButton(onClick = {
                                                    viewModel.isAudioPlaying = !viewModel.isAudioPlaying
                                                }) {
                                                    Icon(
                                                        imageVector = if (viewModel.isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        contentDescription = "play/pause"
                                                    )
                                                }
                                                IconButton(onClick = { viewModel.isAudioPlaying = false }) {
                                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                                                }
                                            }
                                        }
                                    }
                                }

                                // 2. Main Navigation Bar
                                NavigationBar(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.primary
                                ) {
                                    NavigationBarItem(
                                        selected = currentTab == "DASHBOARD",
                                        onClick = { currentTab = "DASHBOARD" },
                                        icon = {
                                            Icon(
                                                imageVector = if (currentTab == "DASHBOARD") Icons.Default.Home else Icons.Outlined.Home,
                                                contentDescription = "Home"
                                            )
                                        },
                                        label = { Text("الرئيسية", fontSize = 11.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = MaterialTheme.colorScheme.primary,
                                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                        )
                                    )

                                    NavigationBarItem(
                                        selected = currentTab == "QURAN",
                                        onClick = { currentTab = "QURAN" },
                                        icon = {
                                            Icon(
                                                imageVector = if (currentTab == "QURAN") Icons.Default.Book else Icons.Outlined.Book,
                                                contentDescription = "Quran"
                                            )
                                        },
                                        label = { Text("المصحف", fontSize = 11.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = MaterialTheme.colorScheme.primary,
                                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                        )
                                    )

                                    NavigationBarItem(
                                        selected = currentTab == "LIBRARY",
                                        onClick = { currentTab = "LIBRARY" },
                                        icon = {
                                            Icon(
                                                imageVector = if (currentTab == "LIBRARY") Icons.Default.AutoStories else Icons.Outlined.AutoStories,
                                                contentDescription = "Library"
                                            )
                                        },
                                        label = { Text("المكتبة", fontSize = 11.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = MaterialTheme.colorScheme.primary,
                                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                        )
                                    )

                                    NavigationBarItem(
                                        selected = currentTab == "AI_SCHOLAR",
                                        onClick = { currentTab = "AI_SCHOLAR" },
                                        icon = {
                                            Icon(
                                                imageVector = if (currentTab == "AI_SCHOLAR") Icons.Default.RecordVoiceOver else Icons.Outlined.RecordVoiceOver,
                                                contentDescription = "AI Scholar"
                                            )
                                        },
                                        label = { Text("الشيخ الذاكر", fontSize = 11.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = MaterialTheme.colorScheme.primary,
                                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                        )
                                    )

                                    NavigationBarItem(
                                        selected = currentTab == "TASBIH",
                                        onClick = { currentTab = "TASBIH" },
                                        icon = {
                                            Icon(
                                                imageVector = if (currentTab == "TASBIH") Icons.Default.Adjust else Icons.Outlined.Adjust,
                                                contentDescription = "Tasbih"
                                            )
                                        },
                                        label = { Text("المسبحة الإعدادات", fontSize = 11.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = MaterialTheme.colorScheme.primary,
                                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                        )
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            when (currentTab) {
                                "DASHBOARD" -> DashboardScreen(viewModel = viewModel) { targetSec ->
                                    currentTab = targetSec
                                }
                                "QURAN" -> QuranAndAzkarTabScreen(viewModel = viewModel)
                                "LIBRARY" -> LibraryAndLessonsTabScreen(viewModel = viewModel)
                                "AI_SCHOLAR" -> AiScholarScreen(viewModel = viewModel)
                                "TASBIH" -> {
                                    Column(modifier = Modifier.fillMaxSize()) {
                                        var localSubTab by remember { mutableStateOf("COUNTER") } // COUNTER, SETTING
                                        TabRow(selectedTabIndex = if(localSubTab == "COUNTER") 0 else 1) {
                                            Tab(
                                                selected = localSubTab == "COUNTER",
                                                onClick = { localSubTab = "COUNTER" },
                                                text = { Text("المسبحة الإكترونية") }
                                            )
                                            Tab(
                                                selected = localSubTab == "SETTING",
                                                onClick = { localSubTab = "SETTING" },
                                                text = { Text("الورد والإعدادات") }
                                            )
                                        }

                                        if (localSubTab == "COUNTER") {
                                            TasbihScreen(viewModel = viewModel)
                                        } else {
                                            SettingsScreen(viewModel = viewModel)
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
}
