package com.example.lab_04

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Adapter class for displaying tasks in a RecyclerView
class TasksAdapter(
    private var tasks: List<Task>, // List of tasks to be displayed
    private val context: Context, // Context for starting new activities and showing Toast messages
    private val lifecycleScope: CoroutineScope // CoroutineScope for managing coroutines in this adapter
) : RecyclerView.Adapter<TasksAdapter.NoteViewHolder>() {

    // Instance of the Room database
    private val db: TaskDatabase = TaskDatabase.getDatabase(context)

    // ViewHolder class to hold references to the views for each task item
    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // TextViews to display task details
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val startTextView: TextView = itemView.findViewById(R.id.startTextView)
        val endTextView: TextView = itemView.findViewById(R.id.endTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)

        // ImageViews for update and delete buttons
        val updateButton: ImageView = itemView.findViewById(R.id.updateButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
    }

    // Create a new ViewHolder for a task item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        // Inflate the task_item layout and create a ViewHolder
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return NoteViewHolder(view)
    }

    // Return the total number of tasks
    override fun getItemCount(): Int = tasks.size

    // Bind task data to the views in the ViewHolder
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val task = tasks[position] // Get the current task
        // Set the task details in the corresponding TextViews
        holder.titleTextView.text = task.title
        holder.contentTextView.text = task.content
        holder.startTextView.text = task.start
        holder.endTextView.text = task.end
        holder.dateTextView.text = task.date

        // Set click listener for the update button
        holder.updateButton.setOnClickListener {
            // Create an Intent to start the UpdateTaskActivity and pass the task ID
            val intent = Intent(holder.itemView.context, UpdateTaskActivity::class.java).apply {
                putExtra("note_id", task.id) // Pass the task ID to the update activity
            }
            holder.itemView.context.startActivity(intent) // Start the update activity
        }

        // Set click listener for the delete button
        holder.deleteButton.setOnClickListener {
            lifecycleScope.launch {
                db.taskDao().delete(task) // Delete the task from the database
                withContext(Dispatchers.Main) {
                    // Show a Toast message to confirm deletion
                    Toast.makeText(holder.itemView.context, "Task Deleted", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Update the list of tasks and notify the adapter of the changes
    @SuppressLint("NotifyDataSetChanged")
    fun refreshData(newTasks: List<Task>) {
        tasks = newTasks // Update the task list
        notifyDataSetChanged() // Notify the adapter to refresh the UI
    }
}
