package com.example.lab_04

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lab_04.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var db: TaskDatabase
    private lateinit var taskDao: TaskDao
    private lateinit var tasksAdapter: TasksAdapter
    private var taskList = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the Room database and Dao for accessing tasks
        db = TaskDatabase.getDatabase(this)
        taskDao = db.taskDao()

        // Setup RecyclerView
        setupRecyclerView()

        // Observe the task count and the last two tasks using LiveData
        observeTaskCount()
        observeLastTwoTasks()

        // Navigate to AddTaskActivity to add a new task
        binding.addButton.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }

        binding.ViewTasks.setOnClickListener {
            val intent = Intent(this, DisplayTaskActivity::class.java)
            startActivity(intent)
        }
    }

    // Setup RecyclerView with an adapter
    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        tasksAdapter = TasksAdapter(emptyList(), this, lifecycleScope) // Start with an empty list
        binding.recyclerView.adapter = tasksAdapter
    }

    // Observe changes in the task count from the database and update the task count in the UI
    @SuppressLint("SetTextI18n")
    private fun observeTaskCount() {
        // Fetch all tasks from the database and observe changes
        taskDao.getAllNotes().observe(this) { tasks ->
            val count = tasks.size // Get the number of tasks
            // Update the UI to show the current task count
            binding.countTextView.text = "Task Count: $count"
        }
    }

    // Observe the last two tasks and update the RecyclerView
    private fun observeLastTwoTasks() {
        taskDao.getAllNotes().observe(this) { tasks ->
            taskList = tasks.toMutableList()
            // Get the last two tasks
            val lastTwoTasks = taskList.takeLast(2)
            // Update the adapter with the last two tasks
            tasksAdapter.refreshData(lastTwoTasks)
        }
    }
}
