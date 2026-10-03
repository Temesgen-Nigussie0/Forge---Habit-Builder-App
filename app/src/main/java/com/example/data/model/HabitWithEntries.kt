package com.example.data.model

import androidx.room.Embedded
import androidx.room.Relation

/**
 * 1-to-many Room relation linking a [Habit] record with its historical [HabitEntry] completion records.
 * Provides a reactive structure for habit progress and streak analysis.
 */
data class HabitWithEntries(
    @Embedded
    val habit: Habit,

    @Relation(
        parentColumn = "id",
        entityColumn = "habitId"
    )
    val entries: List<HabitEntry>
)
