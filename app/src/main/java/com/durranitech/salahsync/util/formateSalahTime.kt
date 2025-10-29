package com.durranitech.salahsync.util

import com.durranitech.salahsync.domain.model.SalahTime
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun SalahTime.formatTime(rawTime: Long): String {
    if (rawTime == 0L) return "--:--"
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(rawTime))
}