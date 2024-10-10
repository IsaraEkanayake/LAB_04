package com.example.lab_04

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "allnotes")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val start: String,
    val end: String,
    val date: String
)
