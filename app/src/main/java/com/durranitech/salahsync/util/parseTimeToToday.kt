package com.durranitech.salahsync.util

import java.util.Calendar

fun parseTimeToToday(timeString: String): Long {
    val (hour, minute) = timeString.split(":").map { it.toInt() }
    return Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}
