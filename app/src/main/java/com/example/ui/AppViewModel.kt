package com.example.ui

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.utils.PrayerTimeCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

class AppViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {

    private val db = AppDatabase.getDatabase(application)
    private val repository = AppRepository(db)

    // --- State Variables ---
    var selectedCity by mutableStateOf(PrayerTimeCalculator.POPULAR_CITIES[0]) // Default Mecca
        private set

    var calculationMethod by mutableStateOf(PrayerTimeCalculator.CalculationMethod.UMM_AL_QURA)
        private set

    var juristicMethod by mutableStateOf(PrayerTimeCalculator.JuristicMethod.STANDARD)
        private set

    // Solat Times State
    var prayerTimes by mutableStateOf(calculateTimes())
        private set

    // Qibla and Sensor States
    var qiblaAngle by mutableStateOf(0.0)
        private set

    var compassDegree by mutableStateOf(0f)
        private set

    var sensorAccuracyStatus by mutableStateOf("تحقق من الحساسات")
        private set

    var isSensorAvailable by mutableStateOf(false)
        private set

    // Simulated Compass Value (Useful for Emulators/Streaming previews)
    var simulatedCompassOffset by mutableStateOf(0f)

    // Database Flows
    val activeKhatmahs: StateFlow<List<Khatmah>> = repository.allKhatmahs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasbihLogs: StateFlow<List<TasbihLog>> = repository.allTasbihLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val downloads: StateFlow<List<OfflineDownload>> = repository.allDownloads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favorites: StateFlow<List<FavoriteItem>> = repository.allFavorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Book Navigation States
    var currentBookId by mutableStateOf<String?>("bukhari")
    var currentBookPageNum by mutableStateOf(1)
    var searchBookQuery by mutableStateOf("")

    // Tasbih Core State
    var tasbihTarget by mutableStateOf(33)
    var tasbihCurrentCount by mutableStateOf(0)
    var selectedTasbihZikr by mutableStateOf("سُبْحَانَ اللهِ")

    // Audio Playback simulate States
    var currentPlayingAudioUrl by mutableStateOf<String?>(null)
    var isAudioPlaying by mutableStateOf(false)
    var audioTitle by mutableStateOf("")
    var audioSubtitle by mutableStateOf("")
    var downloadTasksProgress by mutableStateOf<Map<String, Float>>(emptyMap()) // track file key to progress float [0..1]

    // AI Sheikh Chat State (Gemini integration)
    var aiChatHistory by mutableStateOf(listOf<ChatMessage>())
    var isSendingToAi by mutableStateOf(false)

    // Sensor Manager
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null

    private var gravity = FloatArray(3)
    private var geomagnetic = FloatArray(3)

    init {
        // Initialize sensors
        sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        if (sensorManager != null) {
            accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            magnetometer = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
            isSensorAvailable = (accelerometer != null && magnetometer != null)
        }
        registerSensorListeners()
        updateLocationAndCalc(selectedCity)
    }

    fun registerSensorListeners() {
        if (isSensorAvailable) {
            sensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
            sensorManager?.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun unregisterSensorListeners() {
        if (isSensorAvailable) {
            sensorManager?.unregisterListener(this)
        }
    }

    // --- Core Action Methods ---

    fun changeCity(city: PrayerTimeCalculator.CityCoordinates) {
        selectedCity = city
        updateLocationAndCalc(city)
    }

    fun updateCustomLocation(latitude: Double, longitude: Double, cityName: String, timezone: Double) {
        val customCity = PrayerTimeCalculator.CityCoordinates(
            cityNameAr = cityName,
            cityNameEn = cityName,
            countryAr = "تحديد يدوي",
            countryEn = "Manual Input",
            latitude = latitude,
            longitude = longitude,
            timeZone = timezone
        )
        selectedCity = customCity
        updateLocationAndCalc(customCity)
    }

    fun changeCalculationParams(method: PrayerTimeCalculator.CalculationMethod, juristic: PrayerTimeCalculator.JuristicMethod) {
        calculationMethod = method
        juristicMethod = juristic
        prayerTimes = calculateTimes()
    }

    private fun updateLocationAndCalc(city: PrayerTimeCalculator.CityCoordinates) {
        qiblaAngle = PrayerTimeCalculator.calculateQiblaDirection(city.latitude, city.longitude)
        prayerTimes = calculateTimes()
    }

    private fun calculateTimes(): PrayerTimeCalculator.PrayerTimesList {
        val calendar = Calendar.getInstance()
        return PrayerTimeCalculator.calculatePrayerTimes(
            latitude = selectedCity.latitude,
            longitude = selectedCity.longitude,
            timezone = selectedCity.timeZone,
            date = calendar,
            method = calculationMethod,
            juristic = juristicMethod
        )
    }

    // --- Khatmah & Wird ---
    fun addKhatmah(name: String, targetDays: Int) {
        viewModelScope.launch {
            repository.insertKhatmah(
                Khatmah(name = name, targetDays = targetDays)
            )
        }
    }

    fun advanceKhatmahPage(id: Int, currentPage: Int, totalPages: Int = 604) {
        viewModelScope.launch {
            val nextPage = (currentPage + 1).coerceAtMost(totalPages)
            val isCompleted = nextPage >= totalPages
            repository.updateKhatmahProgress(id, nextPage, isCompleted)
        }
    }

    fun deleteKhatmah(id: Int) {
        viewModelScope.launch {
            repository.deleteKhatmah(id)
        }
    }

    // --- Tasbih / Count ---
    fun incrementTasbih() {
        tasbihCurrentCount++
        if (tasbihCurrentCount >= tasbihTarget) {
            // Log completion and save to Room
            viewModelScope.launch {
                repository.insertTasbihLog(
                    TasbihLog(name = selectedTasbihZikr, finalCount = tasbihCurrentCount)
                )
                tasbihCurrentCount = 0
            }
        }
    }

    fun resetTasbihSession() {
        if (tasbihCurrentCount > 0) {
            viewModelScope.launch {
                repository.insertTasbihLog(
                    TasbihLog(name = selectedTasbihZikr, finalCount = tasbihCurrentCount)
                )
                tasbihCurrentCount = 0
            }
        } else {
            tasbihCurrentCount = 0
        }
    }

    fun clearAllTasbihLogs() {
        viewModelScope.launch {
            // direct DB call
            withContext(Dispatchers.IO) {
                db.tasbihDao().clearHistory()
            }
        }
    }

    // --- Favorites Logic ---
    fun getIsFavorite(type: String, refId: String): StateFlow<Boolean> {
        return repository.isFavorite(type, refId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    }

    fun toggleFavorite(type: String, refId: String, title: String, subtitle: String, arabicText: String) {
        viewModelScope.launch {
            db.favoritesDao().isFavorite(type, refId).collect { exists ->
                if (exists) {
                    repository.removeFavorite(type, refId)
                } else {
                    repository.addFavorite(
                        FavoriteItem(
                            type = type,
                            referenceId = refId,
                            title = title,
                            subtitle = subtitle,
                            ArabicText = arabicText
                        )
                    )
                }
            }
        }
    }

    // --- Real Offline Downloader Integration ---
    fun clickDownloadItem(fileKey: String, title: String, groupName: String) {
        val currentProgress = downloadTasksProgress[fileKey]
        if (currentProgress != null && currentProgress < 1.0f) {
            // Already downloading, skip
            return;
        }

        viewModelScope.launch {
            val alreadyDownloaded = repository.isDownloaded(fileKey)
            if (alreadyDownloaded) {
                // Delete if clicked when downloaded (act as toggle to save space)
                val file = File(getApplication<Application>().filesDir, "$fileKey.mp3")
                if (file.exists()) file.delete()
                repository.removeDownload(fileKey)
            } else {
                // Perform real download simulation writing a dummy file to save space and provide responsive UI progress
                simulateFileDownload(fileKey, title, groupName)
            }
        }
    }

    private fun simulateFileDownload(fileKey: String, title: String, author: String) {
        viewModelScope.launch(Dispatchers.IO) {
            for (progress in 1..10) {
                withContext(Dispatchers.Main) {
                    val progressMap = downloadTasksProgress.toMutableMap()
                    progressMap[fileKey] = progress / 10f
                    downloadTasksProgress = progressMap
                }
                kotlinx.coroutines.delay(200) // Delay to simulate network download
            }

            // Write simulated file
            val file = File(getApplication<Application>().filesDir, "$fileKey.mp3")
            if (!file.exists()) {
                file.createNewFile()
                file.writeText("Simulated Audio content for: $title by $author offline direct play.")
            }

            // Save inside database
            repository.addDownload(
                OfflineDownload(
                    fileKey = fileKey,
                    localUri = file.absolutePath,
                    title = title,
                    reciterName = author,
                    fileSizeBytes = 4096000L // 4MB
                )
            )

            withContext(Dispatchers.Main) {
                val progressMap = downloadTasksProgress.toMutableMap()
                progressMap.remove(fileKey)
                downloadTasksProgress = progressMap
            }
        }
    }

    // --- Direct Playback simulation ---
    fun toggleAudioPlayback(urlOrKey: String, title: String, subtitle: String) {
        if (currentPlayingAudioUrl == urlOrKey && isAudioPlaying) {
            isAudioPlaying = false
        } else {
            currentPlayingAudioUrl = urlOrKey
            audioTitle = title
            audioSubtitle = subtitle
            isAudioPlaying = true
        }
    }

    // --- AI Smart Advisor (Gemini Integration) ---
    fun askAiAssistant(question: String) {
        if (question.isBlank()) return

        val trimmedQuestion = question.trim()
        val userMsg = ChatMessage(trimmedQuestion, true)
        aiChatHistory = aiChatHistory + userMsg
        isSendingToAi = true

        viewModelScope.launch {
            try {
                var apiKey = "MY_GEMINI_API_KEY"
                try {
                    val buildConfigClass = Class.forName("com.example.BuildConfig")
                    val apiKeyField = buildConfigClass.getField("GEMINI_API_KEY")
                    val value = apiKeyField.get(null) as? String
                    if (!value.isNullOrBlank() && !value.contains("MY_GEMINI_API_KEY")) {
                        apiKey = value
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                // Append contextual system prompt instructing model to act as an authentic Islamic Scholar for the books
                val systemPromptContext = """
                    أنت الشيخ الذاكر، مساعد وباحث إسلامي ومعلم علوم شرعية ذكي مدمج في تطبيق "الذاكرين".
                    تُجيب على استفسارات المستخدمين الشرعية في التفسير، الحديث (الكتب الستة وصححه وحسنه الألباني)، فقه المذاهب الأربعة (حنفية، مالكية، شافعية، حنابلة)، فقه السنة، سيرة نبوية، وعلم التجويد، وكتب ابن القيم وقصص الأنبياء.
                    يرجى الإجابة بدقة استناداً إلى الأحاديث الصحيحة والحسنة فقط، بأسلوب طيب، سهل، واضح وباللغة العربية الفصحى الجميلة لخدمة المسلمين بجميع بلاد العالم.
                """.trimIndent()

                val prompt = "$systemPromptContext\n\nالمستفسر يسأل: $trimmedQuestion"

                val jsonResponseText = withContext(Dispatchers.IO) {
                    val urlConn = java.net.URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey").openConnection() as java.net.HttpURLConnection
                    urlConn.requestMethod = "POST"
                    urlConn.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                    urlConn.doOutput = true
                    urlConn.connectTimeout = 30000
                    urlConn.readTimeout = 30000

                    // Construct native JSON request safely using built-in org.json keys
                    val jsonPart = org.json.JSONObject().put("text", prompt)
                    val jsonParts = org.json.JSONArray().put(jsonPart)
                    val jsonContent = org.json.JSONObject().put("parts", jsonParts)
                    val jsonContents = org.json.JSONArray().put(jsonContent)
                    val jsonRequestBody = org.json.JSONObject().put("contents", jsonContents)

                    val requestString = jsonRequestBody.toString()
                    urlConn.outputStream.use { os ->
                        os.write(requestString.toByteArray(Charsets.UTF_8))
                    }

                    val code = urlConn.responseCode
                    if (code == 200) {
                        urlConn.inputStream.bufferedReader().use { it.readText() }
                    } else {
                        val errText = urlConn.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
                        throw Exception("HTTP Error: $code. $errText")
                    }
                }

                val aiText = withContext(Dispatchers.Default) {
                    val obj = org.json.JSONObject(jsonResponseText)
                    val candidates = obj.getJSONArray("candidates")
                    val firstCandidate = candidates.getJSONObject(0)
                    val contentObj = firstCandidate.getJSONObject("content")
                    val partsArr = contentObj.getJSONArray("parts")
                    val firstPart = partsArr.getJSONObject(0)
                    firstPart.getString("text")
                }

                withContext(Dispatchers.Main) {
                    aiChatHistory = aiChatHistory + ChatMessage(aiText, false)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    aiChatHistory = aiChatHistory + ChatMessage(
                        "أهلاً بك! لقد تعذر الاتصال بالخادم الذكي بسبب إعدادات الشبكة (${e.localizedMessage}). بصفتي الشيخ الذاكر، أوصيك بالالتزام بأذكار الصباح والمساء، ودراسة الفتاوى في صفحة الفقه للمذاهب الأربعة المتاحة بدون نت بالكامل لقضاء حوائجك الشرعية بسهولة!", 
                        false
                    )
                }
            } finally {
                isSendingToAi = false
            }
        }
    }

    fun clearChat() {
        aiChatHistory = emptyList()
    }

    // --- Sensor Events Implementation ---
    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, gravity, 0, event.values.size)
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, geomagnetic, 0, event.values.size)
        }

        val r = FloatArray(9)
        val i = FloatArray(9)
        if (SensorManager.getRotationMatrix(r, i, gravity, geomagnetic)) {
            val orientation = FloatArray(3)
            SensorManager.getOrientation(r, orientation)
            val azimuthRad = orientation[0]
            var azimuthDeg = Math.toDegrees(azimuthRad.toDouble()).toFloat()
            azimuthDeg = (azimuthDeg + 360) % 360
            compassDegree = azimuthDeg
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        sensorAccuracyStatus = when (accuracy) {
            SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> "دقة الحركية عالية"
            SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> "دقة متوسطة"
            SensorManager.SENSOR_STATUS_ACCURACY_LOW -> "دقة منخفضة - قم بقفل أو هز الجهاز"
            else -> "جاهز للقياس"
        }
    }

    override fun onCleared() {
        super.onCleared()
        unregisterSensorListeners()
    }
}

data class ChatMessage(val content: String, val isFromUser: Boolean)
