package com.example.data

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Result data class produced by [StreakCalculator].
 */
data class StreakResult(
    val currentStreak: Int,
    val bestStreak: Int,
    val lastCompletedDate: String?,
    val isCompletedToday: Boolean
)

/**
 * Robust, production-grade streak calculation engine.
 * Computes continuous daily streaks, handles yesterday continuity,
 * prevents breaks on today's pending state, and computes historical best streaks.
 */
object StreakCalculator {

    private const val DATE_PATTERN = "yyyy-MM-dd"

    /**
     * Calculates current and best streaks given a collection of completed dates.
     *
     * @param completedDates List of strings in "yyyy-MM-dd" format.
     * @param referenceDate The reference date (defaults to today's date).
     * @param freezeDates Set of dates protected by streak freeze.
     */
    fun calculate(
        completedDates: Collection<String>,
        referenceDate: String = getTodayDateString(),
        freezeDates: Set<String> = emptySet()
    ): StreakResult {
        if (completedDates.isEmpty()) {
            return StreakResult(
                currentStreak = 0,
                bestStreak = 0,
                lastCompletedDate = null,
                isCompletedToday = false
            )
        }

        val completedSet = completedDates.toSet()
        val isCompletedToday = completedSet.contains(referenceDate)
        val sortedDates = completedSet.sorted()
        val lastCompleted = sortedDates.lastOrNull()

        // 1. Calculate Current Streak
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat(DATE_PATTERN, Locale.US)

        try {
            cal.time = sdf.parse(referenceDate) ?: Calendar.getInstance().time
        } catch (_: Exception) {
            // Fallback to current system time
        }

        var currentStreak = 0

        // If today is completed, count it and look at yesterday
        if (isCompletedToday) {
            currentStreak++
            cal.add(Calendar.DAY_OF_YEAR, -1)
        } else {
            // If today is not completed yet, check if yesterday was completed
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val yesterdayStr = sdf.format(cal.time)
            if (!completedSet.contains(yesterdayStr) && !freezeDates.contains(yesterdayStr)) {
                // Streak broken
                currentStreak = 0
            }
        }

        // Count back consecutive past days
        if (currentStreak > 0 || (!isCompletedToday && (completedSet.contains(sdf.format(cal.time)) || freezeDates.contains(sdf.format(cal.time))))) {
            while (true) {
                val checkDate = sdf.format(cal.time)
                if (completedSet.contains(checkDate)) {
                    currentStreak++
                    cal.add(Calendar.DAY_OF_YEAR, -1)
                } else if (freezeDates.contains(checkDate)) {
                    // Protected by freeze, does not break streak
                    cal.add(Calendar.DAY_OF_YEAR, -1)
                } else {
                    break
                }
            }
        }

        // 2. Calculate Best Streak Historically
        var bestStreak = 0
        var runningStreak = 0
        var prevCal: Calendar? = null

        for (dateStr in sortedDates) {
            val dateCal = Calendar.getInstance()
            try {
                dateCal.time = sdf.parse(dateStr) ?: continue
            } catch (_: Exception) {
                continue
            }

            if (prevCal == null) {
                runningStreak = 1
            } else {
                val copyPrev = prevCal.clone() as Calendar
                copyPrev.add(Calendar.DAY_OF_YEAR, 1)

                val expectedNextDayStr = sdf.format(copyPrev.time)
                if (expectedNextDayStr == dateStr) {
                    runningStreak++
                } else {
                    runningStreak = 1
                }
            }
            prevCal = dateCal
            if (runningStreak > bestStreak) {
                bestStreak = runningStreak
            }
        }

        bestStreak = maxOf(bestStreak, currentStreak)

        return StreakResult(
            currentStreak = currentStreak,
            bestStreak = bestStreak,
            lastCompletedDate = lastCompleted,
            isCompletedToday = isCompletedToday
        )
    }

    fun getTodayDateString(): String {
        val sdf = SimpleDateFormat(DATE_PATTERN, Locale.US)
        return sdf.format(Calendar.getInstance().time)
    }
}
