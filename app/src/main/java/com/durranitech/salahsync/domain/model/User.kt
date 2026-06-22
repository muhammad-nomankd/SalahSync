package com.durranitech.salahsync.domain.model

import kotlin.String

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: UserRole? = null,
    val masjid: Masjid? = null,
    val joinedDate: Long = System.currentTimeMillis(),
)