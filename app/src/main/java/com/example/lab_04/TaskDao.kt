package com.example.lab_04

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Query("SELECT * FROM allnotes")
    fun getAllNotes(): LiveData<List<Task>>

    @Update
    suspend fun update(task: Task)

    @Query("SELECT * FROM allnotes WHERE id = :noteId")
    suspend fun getNoteById(noteId: Int): Task?

    @Delete
    suspend fun delete(task: Task)
}

