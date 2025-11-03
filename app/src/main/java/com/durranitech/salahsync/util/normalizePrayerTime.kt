package com.durranitech.salahsync.util

import java.util.Calendar

fun normalizePrayerTime(originalTimeMillis: Long): Long {
    val prayer = Calendar.getInstance().apply { timeInMillis = originalTimeMillis }
    val today = Calendar.getInstance()
    today.set(Calendar.HOUR_OF_DAY, prayer.get(Calendar.HOUR_OF_DAY))
    today.set(Calendar.MINUTE, prayer.get(Calendar.MINUTE))
    today.set(Calendar.SECOND, 0)
    today.set(Calendar.MILLISECOND, 0)
    return today.timeInMillis
}
