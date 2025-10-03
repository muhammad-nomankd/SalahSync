package com.durranitech.salahsync.domain

data class User(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    val role: UserRole = UserRole.MUQTADI
)
