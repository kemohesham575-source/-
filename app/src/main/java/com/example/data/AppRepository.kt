package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val db: AppDatabase) {
    val khatmahDao = db.khatmahDao()
    val tasbihDao = db.tasbihDao()
    val downloadDao = db.downloadDao()
    val favoritesDao = db.favoritesDao()

    // Khatmahs
    val allKhatmahs: Flow<List<Khatmah>> = khatmahDao.getAllKhatmahs()
    suspend fun insertKhatmah(khatmah: Khatmah) = khatmahDao.insertKhatmah(khatmah)
    suspend fun updateKhatmahProgress(id: Int, page: Int, completed: Boolean) =
        khatmahDao.updateKhatmahProgress(id, page, completed)
    suspend fun deleteKhatmah(id: Int) = khatmahDao.deleteKhatmah(id)

    // Tasbih
    val allTasbihLogs: Flow<List<TasbihLog>> = tasbihDao.getAllTasbihLogs()
    fun getTotalTasbihCount(name: String): Flow<Int?> = tasbihDao.getTotalSessionsCount(name)
    suspend fun insertTasbihLog(log: TasbihLog) = tasbihDao.insertLog(log)
    suspend fun clearTasbihHistory() = db.runInTransaction {
        // clear logs
    }

    // Downloads
    val allDownloads: Flow<List<OfflineDownload>> = downloadDao.getAllDownloads()
    suspend fun isDownloaded(key: String): Boolean = downloadDao.isDownloaded(key)
    suspend fun addDownload(download: OfflineDownload) = downloadDao.insertDownload(download)
    suspend fun removeDownload(key: String) = downloadDao.removeDownload(key)

    // Favorites
    val allFavorites: Flow<List<FavoriteItem>> = favoritesDao.getAllFavorites()
    fun isFavorite(type: String, refId: String): Flow<Boolean> = favoritesDao.isFavorite(type, refId)
    suspend fun addFavorite(item: FavoriteItem) = favoritesDao.addFavorite(item)
    suspend fun removeFavorite(type: String, refId: String) = favoritesDao.removeFavorite(type, refId)
}
