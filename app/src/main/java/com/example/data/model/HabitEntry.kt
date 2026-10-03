package com.example.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "habit_entries",
    foreignKeys = [
        ForeignKey(
            entity = Habit::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["habitId", "date"], unique = true),
        Index(value = ["habitId"]),
        Index(value = ["date"]),
        Index(value = ["isCompleted"])
    ]
)
data class HabitEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val habitId: Long,
    val date: String, // Format: yyyy-MM-dd
    val currentCount: Int,
    val targetCount: Int,
    val isCompleted: Boolean,
    val completedAt: Long = System.currentTimeMillis(),
    val notes: String? = null
)
