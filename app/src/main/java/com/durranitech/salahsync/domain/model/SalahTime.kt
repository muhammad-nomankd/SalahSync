package com.durranitech.salahsync.domain.model

data class SalahTime(
    val fajr: Long = 0,
    val dhuhr: Long = 0,
    val asr: Long = 0,
    val maghrib: Long = 0,
    val isha: Long = 0,
    val jummah: Long = 0
)