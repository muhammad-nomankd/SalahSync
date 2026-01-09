package com.durranitech.salahsync.util

import com.durranitech.salahsync.domain.model.NextPrayer
import com.durranitech.salahsync.domain.model.SalahTime
import java.util.Calendar

fun SalahTime.getNextPrayerSimple(): NextPrayer? {
    val now = System.currentTimeMillis()
    val cal = Calendar.getInstance()

    fun timeOf(hour: Int, minute: Int): Long {
        cal.timeInMillis = now
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    val prayers = listOf(
        "Fajr" to timeOf(fajrHour, fajrMinute),
        "Zuhr" to timeOf(dhuhrHour, dhuhrMinute),
        "Asr" to timeOf(asrHour, asrMinute),
        "Maghrib" to timeOf(maghribHour, maghribMinute),
        "Isha" to timeOf(ishaHour, ishaMinute)
    )

    val upcoming = prayers
        .filter { it.second > now }
        .minByOrNull { it.second }

    return upcoming?.let {
        NextPrayer(
            name = it.first,
            atMillis = it.second,
            remainingMillis = it.second - now
        )
    }
}
