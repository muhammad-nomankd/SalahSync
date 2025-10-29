package com.durranitech.salahsync.util

import com.durranitech.salahsync.domain.model.SalahTime

fun SalahTime.toReadableTimes(): Map<String, String> {
    return mapOf(
        "Fajr" to formatTime(fajr),
        "Dhuhr" to formatTime(dhuhr),
        "Asr" to formatTime(asr),
        "Maghrib" to formatTime(maghrib),
        "Isha" to formatTime(isha),
        "Jummah" to formatTime(jummah)
    )
}