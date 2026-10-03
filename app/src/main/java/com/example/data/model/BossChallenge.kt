package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "boss_challenges")
data class BossChallenge(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String = "The Forge Crucible",
    val targetHabitIds: String, // comma-separated habit IDs
    val durationDays: Int = 30, // 21, 30, 66, 75, 100
    val intensity: String = "Extreme", // "Hard", "Extreme", "Legendary"
    val punishment: String, // Cost of failure
    val startDate: String, // yyyy-MM-dd
    val endDate: String,
    val currentDayNumber: Int = 1,
    val completedDays: Int = 0,
    val missedDays: Int = 0,
    val isActive: Boolean = true,
    val isFailed: Boolean = false,
    val isCompleted: Boolean = false,
    val quitReason: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
