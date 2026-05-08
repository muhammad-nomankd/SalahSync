package com.durranitech.salahsync.util

import com.durranitech.salahsync.domain.model.NextPrayer
import com.durranitech.salahsync.domain.model.SalahTime
import java.util.Calendar

fun SalahTime.getNextPrayerSimple(): NextPrayer {
    val now = System.currentTimeMillis()
    val cal = Calendar.getInstance()

    fun timeOf(hour: Int, minute: Int, addDay: Boolean = false): Long {
        cal.timeInMillis = now
        if (addDay) cal.add(Calendar.DAY_OF_YEAR, 1)
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    val todayPrayers = listOf(
        "Fajr" to timeOf(fajrHour?:0, fajrMinute?:0),
        "Zuhr" to timeOf(dhuhrHour?:0, dhuhrMinute?:0),
        "Asr" to timeOf(asrHour?:0, asrMinute?:0),
        "Maghrib" to timeOf(maghribHour?:0, maghribMinute?:0),
        "Isha" to timeOf(ishaHour?:0, ishaMinute?:0)
    )

    val upcomingToday = todayPrayers.firstOrNull { it.second > now }

    val (name, time) = upcomingToday ?: run {
        "Fajr" to timeOf(fajrHour?:0, fajrMinute?:0, addDay = true)
    }

    return NextPrayer(
        name = name,
        atMillis = time,
        remainingMillis = time - now
    )
}
