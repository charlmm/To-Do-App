package com.charles.mvvmto_doapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    fun getTasks(query: String, sortOrder: SortOrder, hideCompleted: Boolean): Flow<List<Task>> =
        when(sortOrder) {
            SortOrder.BY_NAME -> getTaskSortedByName(query, hideCompleted)
            SortOrder.BY_DATE -> getTaskSortedByDate(query, hideCompleted)
        }

    @Query("SELECT * FROM task WHERE (completed != :hideCompleted OR completed = 0) AND name Like '%' || :searchQuery || '%' ORDER BY important DESC, name")
    fun getTaskSortedByName(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE (completed != :hideCompleted OR completed = 0) AND name Like '%' || :searchQuery || '%' ORDER BY important DESC, created")
    fun getTaskSortedByDate(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}