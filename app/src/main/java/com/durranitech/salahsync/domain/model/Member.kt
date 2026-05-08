package com.durranitech.salahsync.domain.model
import kotlin.String


data class Member(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val joinedDate: Long = System.currentTimeMillis()
)
