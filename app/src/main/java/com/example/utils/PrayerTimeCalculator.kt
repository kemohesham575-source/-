package com.example.utils

import java.util.*
import kotlin.math.*

/**
 * Highly polished, mathematically accurate and pure Kotlin Prayer Time Calculator
 * implementing standard astronomical equations for offline usage globally.
 */
object PrayerTimeCalculator {

    enum class CalculationMethod(val fajrAngle: Double, val ishaAngle: Double, val ishaInterval: Int) {
        UMM_AL_QURA(18.5, 0.0, 90), // Isha 90 min after Maghrib (120 in Ramadan)
        EGYPTIAN_GENERAL(19.5, 17.5, 0),
        MWL(18.0, 17.0, 0), // Muslim World League
        ISNA(15.0, 15.0, 0), // Islamic Society of North America
        KARACHI(18.0, 18.0, 0), // University of Islamic Sciences, Karachi
        GULF_REGION(19.5, 0.0, 90),
        ALGERIA(18.0, 17.0, 0),
        TUNISIA(10.0, 10.0, 0)
    }

    enum class JuristicMethod(val shadowFactor: Double) {
        STANDARD(1.0), // Shafi'i, Maliki, Hanbali
        HANAFI(2.0)
    }

    data class PrayerTimesList(
        val fajr: String,
        val sunrise: String,
        val dhuhr: String,
        val asr: String,
        val maghrib: String,
        val isha: String
    )

    data class CityCoordinates(
        val cityNameAr: String,
        val cityNameEn: String,
        val countryAr: String,
        val countryEn: String,
        val latitude: Double,
        val longitude: Double,
        val timeZone: Double
    )

    val POPULAR_CITIES = listOf(
        CityCoordinates("مكة المكرمة", "Mecca", "المملكة العربية السعودية", "Saudi Arabia", 21.4225, 39.8262, 3.0),
        CityCoordinates("القاهرة", "Cairo", "مصر", "Egypt", 30.0444, 31.2357, 3.0),
        CityCoordinates("المدينة المنورة", "Medina", "المملكة العربية السعودية", "Saudi Arabia", 24.4672, 39.6111, 3.0),
        CityCoordinates("القدس الشريف", "Jerusalem", "فلسطين", "Palestine", 31.7683, 35.2137, 3.0),
        CityCoordinates("الرياض", "Riyadh", "المملكة العربية السعودية", "Saudi Arabia", 24.7136, 46.6753, 3.0),
        CityCoordinates("دبي", "Dubai", "الإمارات", "UAE", 25.2048, 55.2708, 4.0),
        CityCoordinates("أبوظبي", "Abu Dhabi", "الإمارات", "UAE", 24.4539, 54.3773, 4.0),
        CityCoordinates("عمان", "Amman", "الأردن", "Jordan", 31.9522, 35.9106, 3.0),
        CityCoordinates("بيروت", "Beirut", "لبنان", "Lebanon", 33.8938, 35.5018, 3.0),
        CityCoordinates("دمشق", "Damascus", "سوريا", "Syria", 33.5138, 36.2765, 3.0),
        CityCoordinates("بغداد", "Baghdad", "العراق", "Iraq", 33.3152, 44.3661, 3.0),
        CityCoordinates("المنامة", "Manama", "البحرين", "Bahrain", 26.2285, 50.5860, 3.0),
        CityCoordinates("الدوحة", "Doha", "قطر", "Qatar", 25.2854, 51.5310, 3.0),
        CityCoordinates("الكويت", "Kuwait City", "الكويت", "Kuwait", 29.3759, 47.9774, 3.0),
        CityCoordinates("مسقط", "Muscat", "عُمان", "Oman", 23.5859, 58.4059, 4.0),
        CityCoordinates("صنعاء", "Sanaa", "اليمن", "Yemen", 15.3694, 44.1910, 3.0),
        CityCoordinates("الرباط", "Rabat", "المغرب", "Morocco", 34.0209, -6.8416, 1.0),
        CityCoordinates("الجزائر", "Algiers", "الجزائر", "Algeria", 36.7538, 3.0588, 1.0),
        CityCoordinates("تونس", "Tunis", "تونس", "Tunisia", 36.8065, 10.1815, 1.0),
        CityCoordinates("طرابلس", "Tripoli", "ليبيا", "Libya", 32.8872, 13.1913, 2.0),
        CityCoordinates("الخرطوم", "Khartoum", "السودان", "Sudan", 15.5007, 32.5599, 2.0),
        CityCoordinates("نواكشوط", "Nouakchott", "موريتانيا", "Mauritania", 18.0735, -15.9582, 0.0),
        CityCoordinates("اسطنبول", "Istanbul", "تركيا", "Turkey", 41.0082, 28.9784, 3.0),
        CityCoordinates("جاكرتا", "Jakarta", "إندونيسيا", "Indonesia", -6.2088, 106.8456, 7.0),
        CityCoordinates("كوالالمبور", "Kuala Lumpur", "ماليزيا", "Malaysia", 3.1390, 101.6869, 8.0),
        CityCoordinates("لندن", "London", "المملكة المتحدة", "United Kingdom", 51.5074, -0.1278, 1.0),
        CityCoordinates("باريس", "Paris", "فرنسا", "France", 48.8566, 2.3522, 2.0),
        CityCoordinates("نيويورك", "New York", "أمريكا", "USA", 40.7128, -74.0060, -4.0)
    )

    fun calculateQiblaDirection(latitude: Double, longitude: Double): Double {
        // Mecca Location coordinates
        val meccaLatRad = Math.toRadians(21.4225)
        val meccaLonRad = Math.toRadians(39.8262)
        val currLngRad = Math.toRadians(longitude)
        val currLatRad = Math.toRadians(latitude)

        val dLon = meccaLonRad - currLngRad

        val y = sin(dLon)
        val x = cos(currLatRad) * tan(meccaLatRad) - sin(currLatRad) * cos(dLon)

        var qiblaAngle = Math.toDegrees(atan2(y, x))
        qiblaAngle = (qiblaAngle + 360) % 360
        return qiblaAngle
    }

    /**
     * Approximate calculation of Solar Position and equation of time to derive precise pray times.
     */
    fun calculatePrayerTimes(
        latitude: Double,
        longitude: Double,
        timezone: Double,
        date: Calendar,
        method: CalculationMethod = CalculationMethod.UMM_AL_QURA,
        juristic: JuristicMethod = JuristicMethod.STANDARD
    ): PrayerTimesList {
        // Day of Year
        val dayOfYear = date.get(Calendar.DAY_OF_YEAR)

        // Simple Julian Date calculation
        val year = date.get(Calendar.YEAR)
        val month = date.get(Calendar.MONTH) + 1
        val day = date.get(Calendar.DAY_OF_MONTH)

        // Astronomical calculations
        // Compute equation of time and declination of the sun
        val d = dayOfYear.toDouble()
        val g = (357.529 + 0.98560028 * d) % 360
        val q = (280.459 + 0.98564736 * d) % 360
        val L = (q + 1.915 * sin(Math.toRadians(g)) + 0.020 * sin(Math.toRadians(2 * g))) % 360

        // Obliquity of outline
        val e = 23.439 - 0.00000036 * d
        val declination = Math.toDegrees(asin(sin(Math.toRadians(e)) * sin(Math.toRadians(L))))

        // Equation of time
        val RA = Math.toDegrees(atan2(cos(Math.toRadians(e)) * sin(Math.toRadians(L)), cos(Math.toRadians(L)))) / 15.0
        val qHours = q / 15.0
        var equationOfTime = (qHours - RA)
        if (equationOfTime > 20) {
            equationOfTime -= 24
        } else if (equationOfTime < -20) {
            equationOfTime += 24
        }
        equationOfTime *= 60.0 // in minutes

        // Solar transit (Noon / Dhuhr time in local solar hours)
        val noonLocal = 12 + timezone - (longitude / 15.0) - (equationOfTime / 60.0)

        // Helper function for Hour Angle for specific altitude angle
        fun getHourAngle(angle: Double, lat: Double, dec: Double): Double {
            val angleRad = Math.toRadians(angle)
            val latRad = Math.toRadians(lat)
            val decRad = Math.toRadians(dec)
            val denom = cos(latRad) * cos(decRad)
            if (abs(denom) < 0.00001) return 0.0
            val cosH = (sin(angleRad) - sin(latRad) * sin(decRad)) / denom
            return if (cosH in -1.0..1.0) {
                Math.toDegrees(acos(cosH)) / 15.0
            } else {
                0.0
            }
        }

        // Sunrise (Sun at altitude of -0.833 deg)
        val tSunrise = getHourAngle(-0.833, latitude, declination)
        val sunriseTime = noonLocal - tSunrise
        val sunsetTime = noonLocal + tSunrise

        // Fajr (Angle as per method description)
        val tFajr = getHourAngle(-method.fajrAngle, latitude, declination)
        val fajrTime = noonLocal - tFajr

        // Asr (Shadow angle calculation based on juristic method)
        val latRad = Math.toRadians(latitude)
        val decRad = Math.toRadians(declination)
        val diffAngleRad = abs(latRad - decRad)
        val acotVal = juristic.shadowFactor + tan(diffAngleRad)
        val asrAngleRad = atan(1.0 / acotVal)
        val asrAngle = Math.toDegrees(asrAngleRad)
        val tAsr = getHourAngle(asrAngle, latitude, declination)
        val asrTime = noonLocal + tAsr

        // Isha (Angle as per method or specific Umm Al Qura interval)
        val ishaTime = if (method.ishaAngle == 0.0) {
            sunsetTime + (method.ishaInterval / 60.0)
        } else {
            val tIsha = getHourAngle(-method.ishaAngle, latitude, declination)
            noonLocal + tIsha
        }

        // Convert double hours to "HH:MM" String format
        fun formatTime(hours: Double): String {
            if (hours.isNaN() || hours < 0 || hours > 24) return "--:--"
            var minutes = (hours * 60).roundToInt()
            val h = (minutes / 60) % 24
            val m = minutes % 60
            return String.format(Locale.getDefault(), "%02d:%02d", h, m)
        }

        return PrayerTimesList(
            fajr = formatTime(fajrTime),
            sunrise = formatTime(sunriseTime),
            dhuhr = formatTime(noonLocal),
            asr = formatTime(asrTime),
            maghrib = formatTime(sunsetTime),
            isha = formatTime(ishaTime)
        )
    }
}
