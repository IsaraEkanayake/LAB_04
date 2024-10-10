package com.example.lab_04

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
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
        binding = ActivityUpdateTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = TaskDatabase.getDatabase(this)

        noteId = intent.getIntExtra("note_id", -1)
        if (noteId == -1) {
            finish()
            return
        }

        loadNote()

        binding.updateButton.setOnClickListener {
            updateTask()
        }
    }

    private fun loadNote() {
        CoroutineScope(Dispatchers.IO).launch {
            val note = db.taskDao().getNoteById(noteId)
            note?.let {
                withContext(Dispatchers.Main) {
                    binding.updateTitleEditText.setText(it.title)
                    binding.updateContentEditText.setText(it.content)

                    // Set the time picker values
                    val startTimeParts = note.start.split(":")
                    if (startTimeParts.size == 2) {
                        binding.updateStartEditText.hour = startTimeParts[0].toInt()
                        binding.updateStartEditText.minute = startTimeParts[1].toInt()
                    }

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

        val updateTask = Task(noteId, newTitle, newContent, newStart, newEnd, newDate)

        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                db.taskDao().update(updateTask) // Update the note in the database
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@UpdateTaskActivity, "Changes Saved", Toast.LENGTH_SHORT).show()
                    scheduleNotification()
                    finish()
                }
            }
        }
    }

    private fun scheduleNotification() {
        val intent = Intent(applicationContext, Notification::class.java)
        val title = binding.updateTitleEditText.text.toString()
        val message = binding.updateContentEditText.text.toString()
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTime()
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
        showAlert(time, title, message)
    }

    private fun showAlert(time: Long, title: String, message: String) {
        val date = Date(time)
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(applicationContext)
        val timeFormat = android.text.format.DateFormat.getTimeFormat(applicationContext)

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

    private fun getTime(): Long {
        val minute = binding.updateEndEditText.minute
        val hour = binding.updateEndEditText.hour
        val day = binding.updateDateEditText.dayOfMonth
        val month = binding.updateDateEditText.month
        val year = binding.updateDateEditText.year

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute)
        return calendar.timeInMillis
    }

    private fun createNotificationChannel() {
        val name = "Notif channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
