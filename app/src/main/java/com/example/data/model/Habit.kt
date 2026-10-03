package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "habits",
    indices = [
        Index(value = ["isArchived"]),
        Index(value = ["category"]),
        Index(value = ["currentStreak"]),
        Index(value = ["createdAt"])
    ]
)
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String, // Health, Mind, Productivity, Energy, Routine, Night, Engineering
    val iconName: String, // water, book, meditation, workout, target, sunlight, sleep, meal, code
    val colorHex: String, // e.g. #FF6A00
    val targetCount: Int = 1,
    val unit: String = "times",
    val timeOfDay: String = "Anytime", // Morning, Afternoon, Evening, Anytime
    val frequency: String = "DAILY", // DAILY, WEEKDAYS, WEEKENDS, CUSTOM
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val lastCompletedDate: String? = null, // Format: yyyy-MM-dd
    val createdAt: Long = System.currentTimeMillis(),
    val isArchived: Boolean = false
)
