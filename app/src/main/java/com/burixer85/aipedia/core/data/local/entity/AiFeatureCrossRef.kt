package com.burixer85.aipedia.core.data.local.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(primaryKeys = ["aiId", "featureId"], indices = [Index("featureId")])
data class AiFeatureCrossRef(
    val aiId: String,
    val featureId: String
)