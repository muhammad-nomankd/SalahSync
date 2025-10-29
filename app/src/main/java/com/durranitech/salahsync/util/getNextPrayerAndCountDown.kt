package com.durranitech.salahsync.util

import com.durranitech.salahsync.domain.model.SalahTime

fun SalahTime.getNextPrayerAndCountdown(): Pair<String, Long>? {
    val now = System.currentTimeMillis()
    val prayers = mapOf(
        "Fajr" to fajr,
        "Dhuhr" to dhuhr,
        "Asr" to asr,
        "Maghrib" to maghrib,
        "Isha" to isha
    ).filterValues { it > now } // upcoming only

    val next = prayers.minByOrNull { it.value } ?: return null
    val remaining = next.value - now
    return next.key to remaining
}