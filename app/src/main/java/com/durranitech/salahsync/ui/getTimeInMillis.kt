package com.durranitech.salahsync.ui

import java.util.Calendar

fun getTimeInMillis(hour: Int, minute: Int): Long =
    Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis