package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.TaskItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, CASE priority WHEN 'Urgent' THEN 1 WHEN 'High' THEN 2 WHEN 'Medium' THEN 3 ELSE 4 END, createdAt DESC")
    fun getAllTasks(): Flow<List<TaskItem>>

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: Long): TaskItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskItem>)

    @Update
    suspend fun updateTask(task: TaskItem)

    @Query("UPDATE tasks SET isCompleted = :completed, completedAt = :completedAt WHERE id = :id")
    suspend fun setTaskCompleted(id: Long, completed: Boolean, completedAt: Long?)

    @Query("UPDATE tasks SET kanbanStatus = :kanbanStatus, isCompleted = :completed, completedAt = :completedAt WHERE id = :id")
    suspend fun updateTaskKanbanStatus(id: Long, kanbanStatus: String, completed: Boolean, completedAt: Long?)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTask(id: Long)
}
