package com.example.lab_04

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.lab_04.databinding.ActivityAddTaskBinding
import kotlinx.coroutines.launch
import java.sql.Date
import java.util.Calendar

class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding
    private lateinit var db: TaskDatabase

    @SuppressLint("DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Room database
        db = TaskDatabase.getDatabase(this)

        createNotificationChannel()

        // Click listener for the save button to save the task in the database
        binding.saveButton.setOnClickListener {
            saveTask()
        }
    }

    private fun saveTask() {
        val title = binding.titleEditText.text.toString()
        val content = binding.contentEditText.text.toString()

        // Get selected time and date from the pickers
        val startTime = getTimeString(binding.startEditText)
        val endTime = getTimeString(binding.endEditText)
        val date = getDateString(binding.dateEditText)

        // Create the Task object
        val task = Task(0, title, content, startTime, endTime, date)

        // Launch a coroutine to insert the task
        lifecycleScope.launch {
            db.taskDao().insert(task)
            Toast.makeText(this@AddTaskActivity, "Task Saved", Toast.LENGTH_SHORT).show()

            // Navigate to DisplayTaskActivity
            val intent = Intent(this@AddTaskActivity, DisplayTaskActivity::class.java)
            startActivity(intent)

            // Schedule notification after saving the task
            scheduleNotification(title, content)

            // Finish this activity if you don't want to keep it in the back stack
            finish()
        }
    }
    private fun getTimeString(timePicker: TimePicker): String {
        val hour = timePicker.hour
        val minute = timePicker.minute
        return String.format("%02d:%02d", hour, minute)
    }

    private fun getDateString(datePicker: DatePicker): String {
        val day = datePicker.dayOfMonth
        val month = datePicker.month + 1 // Month is 0-based
        val year = datePicker.year
        return String.format("%04d-%02d-%02d", year, month, day)
    }

    private fun scheduleNotification(title: String, message: String) {
        val intent = Intent(applicationContext, Notification::class.java).apply {
            putExtra(titleExtra, title)
            putExtra(messageExtra, message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTime() // Calculate time for notification
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
        showAlert(time, title, message) // Show alert after scheduling
    }

    private fun showAlert(time: Long, title: String, message: String) {
        val date = Date(time)
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(applicationContext)
        val timeFormat = android.text.format.DateFormat.getTimeFormat(applicationContext)

        AlertDialog.Builder(this)
            .setTitle("Notification Schedule")
            .setMessage(
                "Title: $title\nMessage: $message\nAt: ${dateFormat.format(date)} ${timeFormat.format(date)}"
            )
            .setPositiveButton("Okay") { _, _ -> }
            .show()
    }

    private fun getTime(): Long {
        val hour = binding.endEditText.hour
        val minute = binding.endEditText.minute
        val day = binding.dateEditText.dayOfMonth
        val month = binding.dateEditText.month
        val year = binding.dateEditText.year

        val calendar = Calendar.getInstance().apply {
            set(year, month, day, hour, minute)
        }
        return calendar.timeInMillis
    }

    private fun createNotificationChannel() {
        val name = "Notification Channel"
        val descriptionText = "A description of the channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
