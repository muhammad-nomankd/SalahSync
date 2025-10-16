package com.durranitech.salahsync.domain.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String? = null,
    val role: UserRole? = null
)