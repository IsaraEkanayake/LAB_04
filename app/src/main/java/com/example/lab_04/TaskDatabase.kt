package com.example.lab_04

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

// Define the Room Database
@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {

    // Abstract method to access the DAO for task operations
    abstract fun taskDao(): TaskDao

    companion object {
        // Volatile ensures that the instance is visible to all threads
        @Volatile
        private var INSTANCE: TaskDatabase? = null


        fun getDatabase(context: Context): TaskDatabase {
            // Check if an instance already exists; if not, create one
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_database" // Name of the database file
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
