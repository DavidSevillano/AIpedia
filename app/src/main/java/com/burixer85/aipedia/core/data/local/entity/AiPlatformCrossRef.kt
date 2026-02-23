package com.burixer85.aipedia.core.data.local.entity

import androidx.room.Entity

@Entity(primaryKeys = ["aiId", "platformId"])
data class AiPlatformCrossRef(
    val aiId: String,
    val platformId: String
)