package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "walnut_progress")
data class WalnutProgressEntity(
    @PrimaryKey
    val walnutId: String,
    val foundCount: Int,
    val updatedAt: Long = System.currentTimeMillis()
)
