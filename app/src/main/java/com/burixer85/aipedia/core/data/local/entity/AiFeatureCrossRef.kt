package com.burixer85.aipedia.core.data.local.entity

import androidx.room.Entity

@Entity(primaryKeys = ["aiId", "featureId"])
data class AiFeatureCrossRef(
    val aiId: String,
    val featureId: String
)