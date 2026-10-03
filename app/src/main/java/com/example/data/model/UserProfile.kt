package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    val id: Int = 1,
    val userName: String = "Alex",
    val headline: String = "CS Student & Visual Creator",
    val bio: String = "Designing software systems, crafting cinematic edits, and engineering daily relentless habits.",
    val bannerUri: String? = null,
    val avatarUri: String? = null,
    val focusArea: String = "Physical Vitality", // Q1
    val currentConsistency: String = "Starting fresh", // Q2
    val frictionHurdle: String = "Lack of time", // Q3
    val targetHabitLoad: String = "3-4 Balanced routine", // Q4
    val primeZone: String = "Early Morning", // Q5
    val starterHabits: String = "Drink 2L Water,Read 15 Pages,10 Min Meditation", // Q6
    val motivationStyle: String = "Streak Counter & Heatmaps", // Q7
    val timeCommitment: String = "15 - 30 minutes", // Q8
    val briefingTime: String = "08:00 AM", // Q9
    val pledgeAccepted: Boolean = true, // Q10
    val isOnboardingCompleted: Boolean = false,
    val occupations: String = "Computer Science, Client Freelancer, Visual Creator",
    val workspaces: String = "Personal Workspace, Creative Studio, Client Work",
    val streakFreezesAvailable: Int = 1,
    val streakFreezesUsed: Int = 0,
    val isComebackProtocolActive: Boolean = false,
    val lastFreezeDate: String? = null,
    val joinedAt: Long = System.currentTimeMillis()
)
