package com.burixer85.aipedia.core.data.local.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(primaryKeys = ["aiId", "platformId"], indices = [Index("platformId")])
data class AiPlatformCrossRef(
    val aiId: String,
    val platformId: String
)