package com.example.lab_04

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lab_04.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var db: TaskDatabase
    private lateinit var taskDao: TaskDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the Room database and Dao for accessing tasks
        db = TaskDatabase.getDatabase(this)
        taskDao = db.taskDao()

        // Observe the task count using LiveData
        observeTaskCount()

        // Navigate to AddTaskActivity to add a new task
        binding.addButton.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }
    }

    // Observe changes in the task list from the database and update the task count in the UI
    // Room database uses LiveData to observe the data in a lifecycle-aware manner
    private fun observeTaskCount() {
        // Fetch all tasks from the database and observe changes
        taskDao.getAllNotes().observe(this) { tasks ->
            val count = tasks.size // Get the number of tasks
            // Update the UI to show the current task count
            binding.countTextView.text = "Task Count: $count"
        }
    }
}
