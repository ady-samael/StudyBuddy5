package com.ady.studybuddy5

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16) // Padding for the layout

            // Button for Pomodoro Timer
            val pomodoroButton = Button(this@MainActivity).apply {
                text = "Pomodoro Timer"
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setOnClickListener {
                    startActivity(Intent(this@MainActivity, PomodoroActivity::class.java)) // Open Pomodoro activity
                }
            }

            // Button for Class Schedule
            val scheduleButton = Button(this@MainActivity).apply {
                text = "Class Schedule"
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setOnClickListener {
                    startActivity(Intent(this@MainActivity, ClassScheduleActivity::class.java)) // Open Class Schedule activity
                }
            }

            // Button for To-Do List
            val todoButton = Button(this@MainActivity).apply {
                text = "To-Do List"
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setOnClickListener {
                    startActivity(Intent(this@MainActivity, TodoListActivity::class.java)) // Open To-Do List activity
                }
            }

            // Add buttons to layout
            addView(pomodoroButton)
            addView(scheduleButton)
            addView(todoButton)
        }

        // Set the content view
        setContentView(rootLayout)
    }
}
