package com.durranitech.salahsync.domain.model

import kotlin.String

data class Announcement(
    val id: String = "",
    val title:  String = "",
    val description: String = "",
    val date: Long = 0L
)
