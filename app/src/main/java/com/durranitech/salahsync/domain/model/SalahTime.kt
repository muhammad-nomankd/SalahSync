package com.durranitech.salahsync.domain.model
data class SalahTime(
    val fajrHour: Int? = null,
    val fajrMinute: Int? = null,
    val dhuhrHour: Int? = null,
    val dhuhrMinute: Int? = null,
    val asrHour: Int? = null,
    val asrMinute: Int? = null,
    val maghribHour: Int? = null,
    val maghribMinute: Int? = null,
    val ishaHour: Int? = null,
    val ishaMinute: Int? = null,
    val jummahHour: Int? = null,
    val jummahMinute: Int? = null
)
