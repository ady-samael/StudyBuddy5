package com.ady.studybuddy5

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

class ScheduleDetailActivity : AppCompatActivity() {

    private lateinit var scheduleList: MutableList<ClassSchedule>
    private lateinit var scheduleAdapter: ScheduleAdapter
    private lateinit var schedulePreferences: SchedulePreferences

    private var startTime: String? = null
    private var endTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_detail)

        // Initialize the SchedulePreferences helper
        schedulePreferences = SchedulePreferences(this)

        // Load the schedules from SharedPreferences
        scheduleList = schedulePreferences.loadSchedules().toMutableList()

        // Sort the schedule list by start time
        sortScheduleList()

        val selectedDay = intent.getStringExtra("selectedDay") ?: "No Day Selected"
        val scheduleTitle = findViewById<TextView>(R.id.scheduleTitle)
        scheduleTitle.text = "Schedule for $selectedDay"

        val recyclerView = findViewById<RecyclerView>(R.id.scheduleRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        scheduleAdapter = ScheduleAdapter(scheduleList) { position -> deleteTimeSlot(position) }
        recyclerView.adapter = scheduleAdapter

        val addTimeSlotButton = findViewById<Button>(R.id.addTimeSlotButton)
        addTimeSlotButton.setOnClickListener {
            showAddTimeSlotDialog(selectedDay)
        }
    }

    private fun sortScheduleList() {
        scheduleList.sortBy { schedule ->
            // Convert the start time to a comparable format (using SimpleDateFormat)
            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
            timeFormat.parse(schedule.startTime)?.time
        }
    }

    private fun showAddTimeSlotDialog(day: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_time_slot, null)
        val courseNameEditText = dialogView.findViewById<EditText>(R.id.courseNameEditText)
        val professorNameEditText = dialogView.findViewById<EditText>(R.id.professorNameEditText)

        val startTimeTextView = dialogView.findViewById<TextView>(R.id.startTimeTextView)
        val endTimeTextView = dialogView.findViewById<TextView>(R.id.endTimeTextView)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Add New Time Slot")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val courseName = courseNameEditText.text.toString()
                val professorName = professorNameEditText.text.toString()

                // Ensure that startTime, endTime, courseName, and professorName are valid
                if (courseName.isNotEmpty() && professorName.isNotEmpty() && startTime != null && endTime != null) {
                    // Create a ClassSchedule object with the startTime and endTime
                    val classSchedule = ClassSchedule(courseName, professorName, startTime ?: "", endTime ?: "")
                    scheduleList.add(classSchedule)

                    // Sort the list after adding a new time slot
                    sortScheduleList()

                    scheduleAdapter.notifyItemInserted(scheduleList.size - 1)

                    // Save the updated list to SharedPreferences
                    schedulePreferences.saveSchedules(scheduleList)
                } else {
                    // Optionally, show an error message if any of the fields are empty
                    showToast("Please fill in all the fields.")
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        startTimeTextView.setOnClickListener {
            showTimePicker { hour, minute ->
                startTime = formatTime(hour, minute)
                startTimeTextView.text = startTime
            }
        }

        endTimeTextView.setOnClickListener {
            showTimePicker { hour, minute ->
                endTime = formatTime(hour, minute)
                endTimeTextView.text = endTime
            }
        }

        dialog.show()
    }

    private fun showTimePicker(onTimeSet: (Int, Int) -> Unit) {
        val calendar = Calendar.getInstance()
        val timePicker = TimePickerDialog(
            this,
            { _: TimePicker, hourOfDay: Int, minute: Int ->
                onTimeSet(hourOfDay, minute)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
        timePicker.show()
    }

    private fun formatTime(hour: Int, minute: Int): String {
        val amPm = if (hour < 12) "AM" else "PM"
        val formattedHour = if (hour > 12) hour - 12 else hour
        val formattedMinute = if (minute < 10) "0$minute" else minute.toString()
        return "$formattedHour:$formattedMinute $amPm"
    }

    private fun deleteTimeSlot(position: Int) {
        scheduleList.removeAt(position)
        scheduleAdapter.notifyItemRemoved(position)

        // Save the updated list to SharedPreferences after deletion
        schedulePreferences.saveSchedules(scheduleList)
    }

    // Utility function to show a toast message
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
