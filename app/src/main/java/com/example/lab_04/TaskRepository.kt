package com.example.lab_04

import androidx.lifecycle.LiveData

class TaskRepository(private val taskDao: TaskDao) {

    // Expose LiveData to observe changes in the task list from the database
    val allTasks: LiveData<List<Task>> = taskDao.getAllNotes()

    // Insert a new task into the database
    suspend fun insert(task: Task) {
        taskDao.insert(task) // Call DAO's insert method to add a task
    }

    // Update an existing task in the database
    suspend fun update(task: Task) {
        taskDao.update(task) // Call DAO's update method to modify a task
    }

    // Delete a task from the database
    suspend fun delete(task: Task) {
        taskDao.delete(task) // Call DAO's delete method to remove a task
    }
}
