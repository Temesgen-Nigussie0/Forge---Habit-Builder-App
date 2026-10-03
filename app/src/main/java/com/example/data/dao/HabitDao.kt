package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.Habit
import com.example.data.model.HabitEntry
import com.example.data.model.HabitWithEntries
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for habit records, daily completion statuses,
 * and streak tracking queries.
 */
@Dao
interface HabitDao {

    // ==========================================
    // 1. HABIT RECORDS
    // ==========================================

    @Query("SELECT * FROM habits WHERE isArchived = 0 ORDER BY createdAt ASC")
    fun getAllActiveHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM habits ORDER BY createdAt ASC")
    fun getAllHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM habits WHERE id = :id LIMIT 1")
    suspend fun getHabitById(id: Long): Habit?

    @Query("SELECT * FROM habits WHERE id = :id LIMIT 1")
    fun getHabitByIdFlow(id: Long): Flow<Habit?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabits(habits: List<Habit>): List<Long>

    @Update
    suspend fun updateHabit(habit: Habit)

    @Query("UPDATE habits SET isArchived = :isArchived WHERE id = :id")
    suspend fun archiveHabit(id: Long, isArchived: Boolean)

    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun deleteHabit(id: Long)

    @Query("DELETE FROM habits")
    suspend fun deleteAllHabits()

    // ==========================================
    // 2. DAILY COMPLETION STATUSES
    // ==========================================

    @Query("SELECT * FROM habit_entries WHERE date = :date")
    fun getEntriesForDate(date: String): Flow<List<HabitEntry>>

    @Query("SELECT * FROM habit_entries WHERE date = :date AND isCompleted = 1")
    fun getCompletedEntriesForDate(date: String): Flow<List<HabitEntry>>

    @Query("SELECT * FROM habit_entries WHERE habitId = :habitId ORDER BY date DESC")
    fun getEntriesForHabit(habitId: Long): Flow<List<HabitEntry>>

    @Query("SELECT * FROM habit_entries ORDER BY date DESC")
    fun getAllEntries(): Flow<List<HabitEntry>>

    @Query("SELECT * FROM habit_entries WHERE habitId = :habitId AND date = :date LIMIT 1")
    suspend fun getEntry(habitId: Long, date: String): HabitEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: HabitEntry): Long

    @Update
    suspend fun updateEntry(entry: HabitEntry)

    @Query("DELETE FROM habit_entries WHERE habitId = :habitId AND date = :date")
    suspend fun deleteEntry(habitId: Long, date: String)

    @Query("DELETE FROM habit_entries WHERE habitId = :habitId")
    suspend fun deleteAllEntriesForHabit(habitId: Long)

    @Query("SELECT * FROM habit_entries WHERE habitId = :habitId AND date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getEntriesBetweenDates(habitId: Long, startDate: String, endDate: String): Flow<List<HabitEntry>>

    @Query("SELECT COUNT(*) FROM habit_entries WHERE date = :date AND isCompleted = 1")
    fun getDailyCompletionCount(date: String): Flow<Int>

    @Query("SELECT date FROM habit_entries WHERE habitId = :habitId AND isCompleted = 1 ORDER BY date DESC")
    fun getCompletedDatesForHabit(habitId: Long): Flow<List<String>>

    @Query("SELECT date FROM habit_entries WHERE habitId = :habitId AND isCompleted = 1 ORDER BY date DESC")
    suspend fun getCompletedDatesForHabitSync(habitId: Long): List<String>

    @Query("SELECT COUNT(*) FROM habit_entries WHERE habitId = :habitId AND isCompleted = 1")
    suspend fun getTotalCompletedCount(habitId: Long): Int

    // ==========================================
    // 3. STREAK TRACKING & RELATIONS
    // ==========================================

    @Query("UPDATE habits SET currentStreak = :currentStreak, bestStreak = :bestStreak, lastCompletedDate = :lastCompletedDate WHERE id = :habitId")
    suspend fun updateHabitStreak(
        habitId: Long,
        currentStreak: Int,
        bestStreak: Int,
        lastCompletedDate: String?
    )

    @Query("SELECT * FROM habits WHERE isArchived = 0 ORDER BY currentStreak DESC")
    fun getHabitsOrderByCurrentStreak(): Flow<List<Habit>>

    @Query("SELECT * FROM habits WHERE isArchived = 0 ORDER BY bestStreak DESC")
    fun getHabitsOrderByBestStreak(): Flow<List<Habit>>

    @Query("SELECT * FROM habits WHERE isArchived = 0 ORDER BY currentStreak DESC LIMIT 1")
    fun getTopStreakHabit(): Flow<Habit?>

    @Transaction
    @Query("SELECT * FROM habits WHERE isArchived = 0 ORDER BY createdAt ASC")
    fun getAllHabitsWithEntries(): Flow<List<HabitWithEntries>>

    @Transaction
    @Query("SELECT * FROM habits WHERE id = :habitId LIMIT 1")
    fun getHabitWithEntries(habitId: Long): Flow<HabitWithEntries?>
}
