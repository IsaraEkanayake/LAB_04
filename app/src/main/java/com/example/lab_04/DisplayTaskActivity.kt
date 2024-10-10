package com.example.lab_04

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lab_04.databinding.ActivityDisplayTaskBinding

class DisplayTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDisplayTaskBinding
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var tasksAdapter: TasksAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the Room database and ViewModel
        val db = TaskDatabase.getDatabase(this)
        val repository = TaskRepository(db.taskDao())
        val factory = TaskViewModelFactory(repository)
        taskViewModel = ViewModelProvider(this, factory).get(TaskViewModel::class.java)

        // Set up RecyclerView and Adapter
        tasksAdapter = TasksAdapter(emptyList(), this, lifecycleScope)
        binding.notsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.notsRecyclerView.adapter = tasksAdapter

        // Add task button click listener
        binding.home.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }

        binding.addButton.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }

        // Observe LiveData and update the RecyclerView
        taskViewModel.allTasks.observe(this) { tasks ->
            if (tasks.isEmpty()) {
                binding.emptyView.visibility = View.VISIBLE
                binding.notsRecyclerView.visibility = View.GONE
            } else {
                binding.emptyView.visibility = View.GONE
                binding.notsRecyclerView.visibility = View.VISIBLE
                tasksAdapter.refreshData(tasks)
            }
        }

        // Implement search functionality
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filter tasks based on the entered text
                val filteredTasks = taskViewModel.allTasks.value?.filter {
                    it.title.contains(newText ?: "", ignoreCase = true)
                } ?: emptyList()
                tasksAdapter.refreshData(filteredTasks)
                return true
            }
        })
    }
}
