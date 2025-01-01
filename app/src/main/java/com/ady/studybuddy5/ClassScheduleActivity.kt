package com.ady.studybuddy5

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ClassScheduleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_schedule)

        // Buttons for each day
        val sundayButton = findViewById<Button>(R.id.sundayButton)
        val mondayButton = findViewById<Button>(R.id.mondayButton)
        val tuesdayButton = findViewById<Button>(R.id.tuesdayButton)
        val wednesdayButton = findViewById<Button>(R.id.wednesdayButton)
        val thursdayButton = findViewById<Button>(R.id.thursdayButton)
        val fridayButton = findViewById<Button>(R.id.fridayButton)
        val saturdayButton = findViewById<Button>(R.id.saturdayButton)

        // Set click listeners for the buttons
        sundayButton.setOnClickListener {
            showSchedule("Sunday")
        }
        mondayButton.setOnClickListener {
            showSchedule("Monday")
        }
        tuesdayButton.setOnClickListener {
            showSchedule("Tuesday")
        }
        wednesdayButton.setOnClickListener {
            showSchedule("Wednesday")
        }
        thursdayButton.setOnClickListener {
            showSchedule("Thursday")
        }
        fridayButton.setOnClickListener {
            showSchedule("Friday")
        }
        saturdayButton.setOnClickListener {
            showSchedule("Saturday")
        }
    }

    private fun showSchedule(day: String) {
        // Display a toast for the selected day
        Toast.makeText(this, "Showing schedule for $day", Toast.LENGTH_SHORT).show()

        // You can also use an Intent to navigate to another activity that shows the detailed schedule
        val intent = Intent(this, ScheduleDetailActivity::class.java).apply {
            putExtra("selectedDay", day)  // Pass the selected day to the next activity
        }
        startActivity(intent)
    }
}
