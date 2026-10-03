package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val notes: String? = null,
    val category: String, // "Software Dev", "Video & Photo", "University", "Client Work", "Personal"
    val priority: String, // "Urgent", "High", "Medium", "Low"
    val scope: String = "Short-Term", // "Short-Term", "Long-Term", "Milestone"
    val dueDate: String? = null, // yyyy-MM-dd
    val endDate: String? = null, // yyyy-MM-dd
    val dailyTimeline: String? = null, // e.g. "10:00 - 12:00 (2h)"
    val estimatedHours: Float = 1.0f,
    val clientOrCourse: String? = null, // e.g. "CS420 Systems", "Client: Nike Spec Ad"
    val tags: String = "", // comma-separated e.g. "React, Figma, 4K Render"
    val kanbanStatus: String = "TODO", // "TODO", "IN_PROGRESS", "REVIEW", "SHIPPED"
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
