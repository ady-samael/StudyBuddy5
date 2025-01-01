package com.ady.studybuddy5

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TodoListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootView = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL

            val todoText = TextView(this@TodoListActivity).apply {
                text = "To-Do List"
                textSize = 24f
            }

            addView(todoText)

            // Add to-do list views and logic here
        }

        setContentView(rootView)
    }
}
