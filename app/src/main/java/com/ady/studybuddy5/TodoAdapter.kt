package com.ady.studybuddy5

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView

class TodoAdapter(private val todoList: List<TodoItem>) :
    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    // ViewHolder class
    inner class TodoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskTextView: TextView
        val deadlineTextView: TextView

        init {
            // Create layout programmatically for each item
            val layout = LinearLayout(view.context).apply {
                orientation = LinearLayout.VERTICAL
            }

            // Initialize TextViews for the task and deadline
            taskTextView = TextView(view.context).apply {
                textSize = 18f
            }
            deadlineTextView = TextView(view.context).apply {
                textSize = 14f
            }

            // Add TextViews to the layout
            layout.addView(taskTextView)
            layout.addView(deadlineTextView)

            // Add layout to itemView
            itemView as LinearLayout
            (itemView as LinearLayout).addView(layout)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        // Create the item view programmatically (a LinearLayout container for each item)
        val itemView = LinearLayout(parent.context).apply {
            orientation = LinearLayout.VERTICAL
        }
        return TodoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todoItem = todoList[position]
        holder.taskTextView.text = todoItem.task
        holder.deadlineTextView.text = todoItem.deadline
    }

    override fun getItemCount(): Int {
        return todoList.size
    }
}
