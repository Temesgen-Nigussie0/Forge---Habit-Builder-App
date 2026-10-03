package com.example.data.model

/**
 * Data projection for habit streak tracking metrics.
 */
data class HabitStreakStats(
    val habitId: Long,
    val habitName: String,
    val currentStreak: Int,
    val bestStreak: Int,
    val totalCompletions: Int,
    val lastCompletedDate: String?,
    val isCompletedToday: Boolean
)
