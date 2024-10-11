package com.example.lab_04

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete

// Data Access Object for managing task database operations
@Dao
interface TaskDao {

    // Inserts a new task into the database.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    // Queries all tasks from the 'allnotes' table and returns a LiveData list to observe data changes in real-time.
    @Query("SELECT * FROM allnotes")
    fun getAllNotes(): LiveData<List<Task>>

    // Updates an existing task in the database.
    @Update
    suspend fun update(task: Task)

    // Retrieves a task by its unique ID. Returns null if no task is found with that ID.
    @Query("SELECT * FROM allnotes WHERE id = :noteId")
    suspend fun getNoteById(noteId: Int): Task?

    // Deletes a task from the database.
    @Delete
    suspend fun delete(task: Task)
}
