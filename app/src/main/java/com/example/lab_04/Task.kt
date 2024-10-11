package com.example.lab_04

import androidx.room.Entity
import androidx.room.PrimaryKey

// Room Entity for the 'allnotes' table in the database
@Entity(tableName = "allnotes")
data class Task(
    // Primary key with auto-generation for unique task ID
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val title: String,

    val content: String,

    val start: String,

    val end: String,

    val date: String
)
