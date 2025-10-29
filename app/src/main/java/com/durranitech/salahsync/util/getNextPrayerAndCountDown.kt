package com.durranitech.salahsync.util

import com.durranitech.salahsync.domain.model.SalahTime


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
