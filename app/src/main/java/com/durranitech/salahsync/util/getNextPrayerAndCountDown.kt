package com.durranitech.salahsync.util

import com.durranitech.salahsync.domain.model.SalahTime
import java.util.Calendar

/*

fun SalahTime.getNextPrayerAndCountdown(): Pair<String, Long>? {
    val currentTime = System.currentTimeMillis()
    val prayers = listOf(
        "Fajr" to fajr,
        "Dhuhr" to dhuhr,
        "Asr" to asr,
        "Maghrib" to maghrib,
        "Isha" to isha
    ).filter { it.second > 0L }

    val nextPrayer = prayers.firstOrNull { it.second > currentTime }
        ?: prayers.firstOrNull()?.copy(second = prayers.first().second + 24 * 60 * 60 * 1000)

    return nextPrayer?.let { (name, time) ->
        val remaining = time - currentTime
        name to remaining.coerceAtLeast(0L)
    }
}
*/

// Util for next prayer calculation, from SalahTime hour/minute model
data class NextPrayer(val name: String, val atMillis: Long, val remainingMillis: Long)

fun SalahTime.getNextPrayer(): NextPrayer? {
    val now = System.currentTimeMillis()
    fun millis(hour: Int, minute: Int): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        if (cal.timeInMillis <= now) cal.add(Calendar.DAY_OF_YEAR, 1)
        return cal.timeInMillis
    }
    val options = listOf(
        "Fajr"    to millis(fajrHour, fajrMinute),
        "Zuhr"    to millis(dhuhrHour, dhuhrMinute),
        "Asr"     to millis(asrHour, asrMinute),
        "Maghrib" to millis(maghribHour, maghribMinute),
        "Isha"    to millis(ishaHour, ishaMinute)
        // You can add Jummah...
    )
    val next = options.minByOrNull { it.second } ?: return null
    val remaining = (next.second - now).coerceAtLeast(0L)
    return NextPrayer(next.first, next.second, remaining)
}

