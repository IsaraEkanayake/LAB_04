package com.example.lab_04


import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.lab_04.databinding.ActivityUpdateTaskBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.sql.Date
import java.util.*

class UpdateTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateTaskBinding
    private lateinit var db: TaskDatabase
    private var noteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateTaskBinding.inflate(layoutInflater) // Inflate the layout
        setContentView(binding.root)

        db = TaskDatabase.getDatabase(this)

        noteId = intent.getIntExtra("note_id", -1)
        if (noteId == -1) {
            finish()
            return
        }

        loadNote() // Load the note data for updating


        binding.updateButton.setOnClickListener {
            updateTask() // Call method to update the task
        }
    }

    // Load the task from the database
    private fun loadNote() {
        CoroutineScope(Dispatchers.IO).launch {
            val note = db.taskDao().getNoteById(noteId) // Retrieve the task by ID
            note?.let {
                withContext(Dispatchers.Main) {
                    // Populate the UI elements with task data
                    binding.updateTitleEditText.setText(it.title)
                    binding.updateContentEditText.setText(it.content)

                    // Set the time picker values for start time
                    val startTimeParts = note.start.split(":")
                    if (startTimeParts.size == 2) {
                        binding.updateStartEditText.hour = startTimeParts[0].toInt()
                        binding.updateStartEditText.minute = startTimeParts[1].toInt()
                    }

                    // Set the time picker values for end time
                    val endTimeParts = note.end.split(":")
                    if (endTimeParts.size == 2) {
                        binding.updateEndEditText.hour = endTimeParts[0].toInt()
                        binding.updateEndEditText.minute = endTimeParts[1].toInt()
                    }

                    // Set the date picker value
                    val dateParts = note.date.split("-")
                    if (dateParts.size == 3) {
                        binding.updateDateEditText.updateDate(dateParts[0].toInt(), dateParts[1].toInt() - 1, dateParts[2].toInt()) // month is 0-indexed
                    }
                }
            }
        }
    }

    // Update the task in the database
    private fun updateTask() {
        val newTitle = binding.updateTitleEditText.text.toString()
        val newContent = binding.updateContentEditText.text.toString()

        // Get time from TimePicker
        val newStartHour = binding.updateStartEditText.hour
        val newStartMinute = binding.updateStartEditText.minute
        val newStart = String.format("%02d:%02d", newStartHour, newStartMinute)

        val newEndHour = binding.updateEndEditText.hour
        val newEndMinute = binding.updateEndEditText.minute
        val newEnd = String.format("%02d:%02d", newEndHour, newEndMinute)

        // Get date from DatePicker
        val newDate = "${binding.updateDateEditText.year}-${binding.updateDateEditText.month + 1}-${binding.updateDateEditText.dayOfMonth}" // month is 0-indexed

        // Create a new Task object with updated values
        val updateTask = Task(noteId, newTitle, newContent, newStart, newEnd, newDate)

        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                db.taskDao().update(updateTask) // Update the note in the database
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@UpdateTaskActivity, "Changes Saved", Toast.LENGTH_SHORT).show() // Show success message
                    scheduleNotification() // Schedule notification for the updated task
                    finish()
                }
            }
        }
    }

    // Schedule a notification for the task
    private fun scheduleNotification() {
        val intent = Intent(applicationContext, Notification::class.java) // Create intent for notification
        val title = binding.updateTitleEditText.text.toString() // Get title for notification
        val message = binding.updateContentEditText.text.toString() // Get message for notification
        intent.putExtra(titleExtra, title) // Put title in intent
        intent.putExtra(messageExtra, message) // Put message in intent

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        ) // Create a PendingIntent for the notification

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager // Get AlarmManager service
        val time = getTime() // Get the time for the notification
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent // Schedule the notification
        )
        showAlert(time, title, message) // Show alert for scheduled notification
    }

    // Show alert dialog with notification details
    private fun showAlert(time: Long, title: String, message: String) {
        val date = Date(time) // Create Date object for the scheduled time
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(applicationContext) // Get date format
        val timeFormat = android.text.format.DateFormat.getTimeFormat(applicationContext) // Get time format

        // Build and show the alert dialog
        AlertDialog.Builder(this)
            .setTitle("Notification Schedule")
            .setMessage(
                "Title: " + title +
                        "\nMessage: " + message +
                        "\nAt: " + dateFormat.format(date) + " " + timeFormat.format(date)
            )
            .setPositiveButton("Okay") { _, _ -> }
            .show()
    }

    // Get the time for the notification from the DatePicker and TimePickers
    private fun getTime(): Long {
        val minute = binding.updateEndEditText.minute
        val hour = binding.updateEndEditText.hour
        val day = binding.updateDateEditText.dayOfMonth
        val month = binding.updateDateEditText.month
        val year = binding.updateDateEditText.year

        // Create a Calendar instance and set the time
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute)
        return calendar.timeInMillis // Return the time in milliseconds
    }

    // Create a notification channel for API 26+
    private fun createNotificationChannel() {
        val name = "Notif channel" // Name of the channel
        val desc = "A Description of the Channel" // Description of the channel
        val importance = NotificationManager.IMPORTANCE_DEFAULT // Importance level
        val channel = NotificationChannel(channelID, name, importance) // Create the channel
        channel.description = desc // Set the channel description
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel) // Register the channel
    }
}
