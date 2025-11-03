package com.durranitech.salahsync.domain.model
data class SalahTime(
    val fajrHour: Int = 5,
    val fajrMinute: Int = 30,
    val dhuhrHour: Int = 12,
    val dhuhrMinute: Int = 30,
    val asrHour: Int = 15,
    val asrMinute: Int = 45,
    val maghribHour: Int = 18,
    val maghribMinute: Int = 15,
    val ishaHour: Int = 20,
    val ishaMinute: Int = 0,
    val jummahHour: Int = 13,
    val jummahMinute: Int = 30
)
