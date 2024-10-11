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

        // Initialize Room database instance
        // Room provides an abstraction layer over SQLite to allow for more robust database access.
        db = TaskDatabase.getDatabase(this)

        createNotificationChannel()

        // Set click listener for save button to save task into the Room database
        binding.saveButton.setOnClickListener {
            saveTask()
        }
    }

    private fun saveTask() {
        // Fetch task details from the UI inputs
        val title = binding.titleEditText.text.toString()
        val content = binding.contentEditText.text.toString()

        // Get time and date values from time/date pickers
        val startTime = getTimeString(binding.startEditText)
        val endTime = getTimeString(binding.endEditText)
        val date = getDateString(binding.dateEditText)

        // Create a Task object with collected data
        val task = Task(0, title, content, startTime, endTime, date)

        // Use a coroutine to perform database operations off the main thread
        lifecycleScope.launch {
            db.taskDao().insert(task) // Insert task into the Room database
            Toast.makeText(this@AddTaskActivity, "Task Saved", Toast.LENGTH_SHORT).show()

            // After saving, navigate to DisplayTaskActivity
            val intent = Intent(this@AddTaskActivity, DisplayTaskActivity::class.java)
            startActivity(intent)

            // Schedule a notification for the saved task
            scheduleNotification(title, content)

            finish()
        }
    }

    // Helper function to get time in string format from TimePicker
    private fun getTimeString(timePicker: TimePicker): String {
        val hour = timePicker.hour
        val minute = timePicker.minute
        return String.format("%02d:%02d", hour, minute)
    }

    // Helper function to get date in string format from DatePicker
    private fun getDateString(datePicker: DatePicker): String {
        val day = datePicker.dayOfMonth
        val month = datePicker.month + 1 // Month is 0-based
        val year = datePicker.year
        return String.format("%04d-%02d-%02d", year, month, day)
    }

    // Schedule a notification for the task
    // Notification will trigger at the time set in getTime() method
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

        // Get AlarmManager to set an exact alarm to trigger the notification
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTime()
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
        showAlert(time, title, message) // Display an alert after scheduling notification
    }

    // Show confirmation dialog after scheduling the notification
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

    // Get the time for notification from the selected date and time pickers
    private fun getTime(): Long {
        val hour = binding.endEditText.hour
        val minute = binding.endEditText.minute
        val day = binding.dateEditText.dayOfMonth
        val month = binding.dateEditText.month
        val year = binding.dateEditText.year

        // Create a Calendar instance to set the time for notification
        val calendar = Calendar.getInstance().apply {
            set(year, month, day, hour, minute)
        }
        return calendar.timeInMillis
    }

    // Create a notification channel to send notifications
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
