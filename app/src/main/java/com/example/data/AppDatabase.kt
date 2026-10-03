package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.BossChallengeDao
import com.example.data.dao.HabitDao
import com.example.data.dao.TaskDao
import com.example.data.dao.UserProfileDao
import com.example.data.model.BossChallenge
import com.example.data.model.Habit
import com.example.data.model.HabitEntry
import com.example.data.model.TaskItem
import com.example.data.model.UserProfile

@Database(
    entities = [
        Habit::class,
        HabitEntry::class,
        UserProfile::class,
        TaskItem::class,
        BossChallenge::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun taskDao(): TaskDao
    abstract fun bossChallengeDao(): BossChallengeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "forge_habits.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
