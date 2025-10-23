package com.durranitech.salahsync.domain.model

data class Masjid(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val city: String = "",
    val code: String = "",
    val imageUrl: String = "",
    val imamName: String = "",
    val imamId: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    val salahTimes: List<SalahTime> = emptyList()
)
