package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface KhatmahDao {
    @Query("SELECT * FROM khatmahs ORDER BY id DESC")
    fun getAllKhatmahs(): Flow<List<Khatmah>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKhatmah(khatmah: Khatmah)

    @Query("UPDATE khatmahs SET currentPage = :page, completed = :completed WHERE id = :id")
    suspend fun updateKhatmahProgress(id: Int, page: Int, completed: Boolean)

    @Query("DELETE FROM khatmahs WHERE id = :id")
    suspend fun deleteKhatmah(id: Int)
}

@Dao
interface TasbihDao {
    @Query("SELECT * FROM tasbih_logs ORDER BY timestamp DESC")
    fun getAllTasbihLogs(): Flow<List<TasbihLog>>

    @Query("SELECT SUM(finalCount) FROM tasbih_logs WHERE name = :name")
    fun getTotalSessionsCount(name: String): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: TasbihLog)

    @Query("DELETE FROM tasbih_logs")
    suspend fun clearHistory()
}

@Dao
interface DownloadDao {
    @Query("SELECT * FROM offline_downloads ORDER BY timestamp DESC")
    fun getAllDownloads(): Flow<List<OfflineDownload>>

    @Query("SELECT EXISTS(SELECT 1 FROM offline_downloads WHERE fileKey = :key LIMIT 1)")
    suspend fun isDownloaded(key: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(download: OfflineDownload)

    @Query("DELETE FROM offline_downloads WHERE fileKey = :key")
    suspend fun removeDownload(key: String)
}

@Dao
interface FavoritesDao {
    @Query("SELECT * FROM favorites ORDER BY timestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteItem>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE type = :type AND referenceId = :refId LIMIT 1)")
    fun isFavorite(type: String, refId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(item: FavoriteItem)

    @Query("DELETE FROM favorites WHERE type = :type AND referenceId = :refId")
    suspend fun removeFavorite(type: String, refId: String)
}
