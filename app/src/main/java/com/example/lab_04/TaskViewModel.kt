package com.example.lab_04

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import kotlinx.coroutines.launch

// ViewModel class to manage the UI-related data for the task management
class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
    // LiveData object holding a list of all tasks from the repository
    val allTasks: LiveData<List<Task>> = repository.allTasks

    // Function to insert a new task
    fun insert(task: Task) = viewModelScope.launch {
        // Launch a coroutine to perform the insert operation
        repository.insert(task)
    }

    // Function to update an existing task
    fun update(task: Task) = viewModelScope.launch {
        // Launch a coroutine to perform the update operation
        repository.update(task)
    }

    // Function to delete a task
    fun delete(task: Task) = viewModelScope.launch {
        // Launch a coroutine to perform the delete operation
        repository.delete(task)
    }
}
