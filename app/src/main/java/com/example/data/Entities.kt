package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "khatmahs")
data class Khatmah(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val targetDays: Int,
    val startTimestamp: Long = System.currentTimeMillis(),
    val currentPage: Int = 1,
    val completed: Boolean = false
)

@Entity(tableName = "tasbih_logs")
data class TasbihLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val finalCount: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "offline_downloads")
data class OfflineDownload(
    @PrimaryKey val fileKey: String, // e.g. "reciter_rashid_meshary_001" or "lecture_shaarawy_01"
    val localUri: String,
    val title: String,
    val reciterName: String,
    val fileSizeBytes: Long,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorites")
data class FavoriteItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "SURAH", "VERSE", "HADITH", "BOOK_PAGE", "LECTURE"
    val referenceId: String, // identifier to query or locate
    val title: String,
    val subtitle: String,
    val ArabicText: String,
    val timestamp: Long = System.currentTimeMillis()
)
