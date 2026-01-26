package com.durranitech.salahsync.util

fun formatCountdown(millis: Long): String {
    if (millis <= 0) return "00:00:00"
    val totalSeconds = millis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}
