package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.StreakCalculator
import com.example.data.dao.HabitDao
import com.example.data.model.Habit
import com.example.data.model.HabitEntry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class HabitDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var habitDao: HabitDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        habitDao = database.habitDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `insert and retrieve habit records`() = runTest {
        val habit = Habit(
            name = "Morning Code Practice",
            category = "Engineering",
            iconName = "code",
            colorHex = "#FF6A00",
            targetCount = 1,
            unit = "session",
            timeOfDay = "Morning",
            frequency = "DAILY"
        )

        val id = habitDao.insertHabit(habit)
        assertTrue(id > 0)

        val retrieved = habitDao.getHabitById(id)
        assertNotNull(retrieved)
        assertEquals("Morning Code Practice", retrieved?.name)
        assertEquals("Engineering", retrieved?.category)
        assertEquals(0, retrieved?.currentStreak)

        val allActive = habitDao.getAllActiveHabits().first()
        assertEquals(1, allActive.size)
    }

    @Test
    fun `log daily completion status and query entries`() = runTest {
        val habitId = habitDao.insertHabit(
            Habit(
                name = "Drink 2L Water",
                category = "Health",
                iconName = "water",
                colorHex = "#007AFF",
                targetCount = 4,
                unit = "glasses"
            )
        )

        // Day 1: completed
        habitDao.insertEntry(
            HabitEntry(
                habitId = habitId,
                date = "2026-10-01",
                currentCount = 4,
                targetCount = 4,
                isCompleted = true
            )
        )

        // Day 2: partially completed
        habitDao.insertEntry(
            HabitEntry(
                habitId = habitId,
                date = "2026-10-02",
                currentCount = 2,
                targetCount = 4,
                isCompleted = false
            )
        )

        val day1Entries = habitDao.getEntriesForDate("2026-10-01").first()
        assertEquals(1, day1Entries.size)
        assertTrue(day1Entries.first().isCompleted)

        val day1Completed = habitDao.getCompletedEntriesForDate("2026-10-01").first()
        assertEquals(1, day1Completed.size)

        val day2Completed = habitDao.getCompletedEntriesForDate("2026-10-02").first()
        assertEquals(0, day2Completed.size)

        val completedDates = habitDao.getCompletedDatesForHabitSync(habitId)
        assertEquals(listOf("2026-10-01"), completedDates)
    }

    @Test
    fun `streak tracking calculation and update`() = runTest {
        val habitId = habitDao.insertHabit(
            Habit(
                name = "Read 15 Pages",
                category = "Mind",
                iconName = "book",
                colorHex = "#FF9500",
                targetCount = 1
            )
        )

        val dates = listOf("2026-09-28", "2026-09-29", "2026-09-30", "2026-10-01", "2026-10-02")
        for (d in dates) {
            habitDao.insertEntry(
                HabitEntry(
                    habitId = habitId,
                    date = d,
                    currentCount = 1,
                    targetCount = 1,
                    isCompleted = true
                )
            )
        }

        val completedDates = habitDao.getCompletedDatesForHabitSync(habitId)
        assertEquals(5, completedDates.size)

        val streakResult = StreakCalculator.calculate(
            completedDates = completedDates,
            referenceDate = "2026-10-02"
        )
        assertEquals(5, streakResult.currentStreak)
        assertEquals(5, streakResult.bestStreak)
        assertTrue(streakResult.isCompletedToday)

        habitDao.updateHabitStreak(
            habitId = habitId,
            currentStreak = streakResult.currentStreak,
            bestStreak = streakResult.bestStreak,
            lastCompletedDate = streakResult.lastCompletedDate
        )

        val updatedHabit = habitDao.getHabitById(habitId)
        assertEquals(5, updatedHabit?.currentStreak)
        assertEquals(5, updatedHabit?.bestStreak)
        assertEquals("2026-10-02", updatedHabit?.lastCompletedDate)
    }

    @Test
    fun `habit with entries relation and cascade delete`() = runTest {
        val habitId = habitDao.insertHabit(
            Habit(
                name = "Cold Shower",
                category = "Energy",
                iconName = "shower",
                colorHex = "#34C759"
            )
        )

        habitDao.insertEntry(
            HabitEntry(
                habitId = habitId,
                date = "2026-10-02",
                currentCount = 1,
                targetCount = 1,
                isCompleted = true
            )
        )

        val habitWithEntries = habitDao.getHabitWithEntries(habitId).first()
        assertNotNull(habitWithEntries)
        assertEquals("Cold Shower", habitWithEntries?.habit?.name)
        assertEquals(1, habitWithEntries?.entries?.size)

        // Delete habit -> cascade deletes entries
        habitDao.deleteHabit(habitId)

        val remainingEntries = habitDao.getEntriesForHabit(habitId).first()
        assertTrue(remainingEntries.isEmpty())
    }
}
