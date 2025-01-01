package com.ady.studybuddy5

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ArchiveActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var archiveAdapter: TodoAdapter
    private lateinit var archiveList: MutableList<TodoItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archive)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewArchive)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Load archived tasks
        archiveList = loadArchivedTasks(this).toMutableList()

        // Initialize and set the adapter for RecyclerView
        archiveAdapter = TodoAdapter(archiveList, onFinish = null) // No "Finish" button for archived tasks
        recyclerView.adapter = archiveAdapter
    }

    // Function to load archived tasks from SharedPreferences
    private fun loadArchivedTasks(context: Context): List<TodoItem> {
        val sharedPreferences = context.getSharedPreferences("ArchivedTodoPrefs", Context.MODE_PRIVATE)
        val archivedJson = sharedPreferences.getString("archived_todo_list", "") ?: return emptyList()

        // Safely parse the CSV-like string into TodoItems
        return archivedJson.split(";").mapNotNull {
            val parts = it.split(",")
            if (parts.size == 3) {
                TodoItem(parts[0], parts[1], parts[2])
            } else {
                null // Skip invalid entries
            }
        }
    }

    // Optionally, you can add a method to save archived tasks back into shared preferences if needed.
    private fun saveArchivedTasks(context: Context, archivedTasks: List<TodoItem>) {
        val sharedPreferences = context.getSharedPreferences("ArchivedTodoPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Convert list of archived tasks to a CSV-like string
        val archivedJson = archivedTasks.joinToString(separator = ";") {
            "${it.task},${it.description},${it.deadline}" // Format as CSV-like string
        }

        // Save the string in SharedPreferences
        editor.putString("archived_todo_list", archivedJson)
        editor.apply()
    }
}
