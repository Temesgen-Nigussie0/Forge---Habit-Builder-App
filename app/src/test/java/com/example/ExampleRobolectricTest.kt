package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Forge Habits", appName)
  }

  @Test
  fun `database creates in memory and stores habit`() = kotlinx.coroutines.test.runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(context, com.example.data.AppDatabase::class.java).build()
    val repo = com.example.data.HabitRepository(
      db.habitDao(),
      db.userProfileDao(),
      db.taskDao(),
      db.bossChallengeDao()
    )

    val profile = com.example.data.model.UserProfile(
      userName = "Tester",
      focusArea = "Physical Vitality"
    )
    repo.completeOnboarding(profile, listOf("Drink 2L Water", "Read 15 Pages"))

    val habits = repo.allHabits.first()
    assertEquals(2, habits.size)

    val tasks = repo.allTasks.first()
    assert(tasks.isNotEmpty())

    // Test Boss Challenge initiation
    repo.startBossChallenge(
      com.example.data.model.BossChallenge(
        title = "Sprint 21",
        targetHabitIds = "${habits.first().id}",
        durationDays = 21,
        intensity = "Extreme",
        punishment = "Cold plunge at 5 AM",
        startDate = "2026-10-01",
        endDate = "2026-10-22"
      )
    )
    val activeChallenge = repo.activeBossChallenge.first()
    assert(activeChallenge != null)
    assertEquals(21, activeChallenge?.durationDays)
    db.close()
  }
}
