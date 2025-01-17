package com.ady.studybuddy5

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.app.AlertDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class TodoListActivity : AppCompatActivity() {

    private lateinit var todoAdapter: TodoAdapter
    private lateinit var todoList: MutableList<TodoItem>
    private lateinit var archiveList: MutableList<TodoItem>

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddTask: FloatingActionButton
    private lateinit var btnGoToArchive: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todolist)

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView)
        fabAddTask = findViewById(R.id.fabAddTask)
        btnGoToArchive = findViewById(R.id.btnGoToArchive)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Load to-do and archive lists
        todoList = loadTodoList(this).toMutableList().sortedBy { parseDate(it.deadline) }.toMutableList()
        archiveList = loadArchiveList(this).toMutableList()

        // Initialize the adapter
        todoAdapter = TodoAdapter(todoList) { task -> markTaskAsFinished(task) }
        recyclerView.adapter = todoAdapter

        // Add task button listener
        fabAddTask.setOnClickListener { showAddTaskDialog() }

        // Archive button listener
        btnGoToArchive.setOnClickListener {
            startActivity(Intent(this, ArchiveActivity::class.java))
        }
    }

    private fun showAddTaskDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_task, null)
        val taskInput = dialogView.findViewById<EditText>(R.id.taskInput)
        val descriptionInput = dialogView.findViewById<EditText>(R.id.descriptionInput)
        val datePicker = dialogView.findViewById<DatePicker>(R.id.datePicker)
        val timePicker = dialogView.findViewById<TimePicker>(R.id.timePicker)

        timePicker.setIs24HourView(true)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Add New Task")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val task = taskInput.text.toString()
                val description = descriptionInput.text.toString()
                val date = "${datePicker.year}-${datePicker.month + 1}-${datePicker.dayOfMonth}"
                val time = String.format("%02d:%02d", timePicker.hour, timePicker.minute)
                val deadline = "$date $time"

                if (task.isNotEmpty() && description.isNotEmpty() && deadline.isNotEmpty()) {
                    val newTodo = TodoItem(task, description, deadline)

                    todoList.add(newTodo)
                    todoList.sortBy { parseDate(it.deadline) }

                    saveTodoList(this, todoList)

                    todoAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun markTaskAsFinished(task: TodoItem) {
        todoList.remove(task)

        val completedTask = task.copy(isCompleted = true)
        archiveList.add(completedTask)

        saveTodoList(this, todoList)
        saveArchiveList(this, archiveList)

        todoAdapter.notifyDataSetChanged()

        Toast.makeText(this, "Task marked as finished and archived", Toast.LENGTH_SHORT).show()
    }

    private fun saveTodoList(context: Context, todos: List<TodoItem>) {
        val sharedPreferences = context.getSharedPreferences("TodoPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val todoJson = todos.joinToString(separator = ";") {
            "${it.task},${it.description},${it.deadline},${it.isCompleted}"
        }
        editor.putString("todo_list", todoJson)
        editor.apply()
    }

    private fun saveArchiveList(context: Context, archive: List<TodoItem>) {
        val sharedPreferences = context.getSharedPreferences("ArchivePrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val archiveJson = archive.joinToString(separator = ";") {
            "${it.task},${it.description},${it.deadline},${it.isCompleted}"
        }
        editor.putString("archive_list", archiveJson)
        editor.apply()
    }

    private fun loadTodoList(context: Context): List<TodoItem> {
        val sharedPreferences = context.getSharedPreferences("TodoPrefs", Context.MODE_PRIVATE)
        val todoJson = sharedPreferences.getString("todo_list", "") ?: return emptyList()
        return todoJson.split(";").mapNotNull {
            val parts = it.split(",")
            if (parts.size == 4) {
                TodoItem(parts[0], parts[1], parts[2], parts[3].toBoolean())
            } else null
        }
    }

    private fun loadArchiveList(context: Context): List<TodoItem> {
        val sharedPreferences = context.getSharedPreferences("ArchivePrefs", Context.MODE_PRIVATE)
        val archiveJson = sharedPreferences.getString("archive_list", "") ?: return emptyList()
        return archiveJson.split(";").mapNotNull {
            val parts = it.split(",")
            if (parts.size == 4) {
                TodoItem(parts[0], parts[1], parts[2], parts[3].toBoolean())
            } else null
        }
    }

    private fun parseDate(dateString: String): Date {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return format.parse(dateString) ?: Date()
    }
}
