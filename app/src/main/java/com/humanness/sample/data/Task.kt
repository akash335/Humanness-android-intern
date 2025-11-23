package com.humanness.sample.data

import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: Long,
    val taskType: String,
    val text: String? = null,
    val imageUrl: String? = null,
    val imagePath: String? = null,
    val audioPath: String,
    val durationSec: Int,
    val timestamp: String
)