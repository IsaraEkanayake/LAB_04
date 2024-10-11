package com.example.lab_04

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// ViewModelFactory class to create instances of TaskViewModel
class TaskViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    // Method to create ViewModel instances
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the requested ViewModel class is TaskViewModel
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") // Suppress warning for unchecked cast
            return TaskViewModel(repository) as T // Return a TaskViewModel instance
        }
        // Throw an exception if the ViewModel class is unknown
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
