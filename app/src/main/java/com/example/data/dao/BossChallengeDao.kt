package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BossChallenge
import kotlinx.coroutines.flow.Flow

@Dao
interface BossChallengeDao {
    @Query("SELECT * FROM boss_challenges WHERE isActive = 1 LIMIT 1")
    fun getActiveChallenge(): Flow<BossChallenge?>

    @Query("SELECT * FROM boss_challenges WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveChallengeSync(): BossChallenge?

    @Query("SELECT * FROM boss_challenges ORDER BY createdAt DESC")
    fun getAllChallenges(): Flow<List<BossChallenge>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenge(challenge: BossChallenge): Long

    @Update
    suspend fun updateChallenge(challenge: BossChallenge)

    @Query("UPDATE boss_challenges SET isActive = 0, isFailed = 1, quitReason = :reason WHERE id = :id")
    suspend fun quitChallenge(id: Long, reason: String)
}
