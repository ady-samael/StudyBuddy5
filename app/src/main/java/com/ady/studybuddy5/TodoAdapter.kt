package com.ady.studybuddy5

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TodoAdapter(
    private val todoList: MutableList<TodoItem>,
    private val onFinish: ((TodoItem) -> Unit)? = null // Optional onFinish callback
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    // ViewHolder class
    inner class TodoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskTextView: TextView = TextView(view.context).apply {
            textSize = 18f
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                2f // Adjusting to give more space to the task
            )
        }
        val deadlineTextView: TextView = TextView(view.context).apply {
            textSize = 14f
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f // Adjusting for the deadline to take less space
            )
        }
        val finishButton: Button = Button(view.context).apply {
            text = "Finish"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            visibility = if (onFinish == null) View.GONE else View.VISIBLE
        }

        init {
            val layout = LinearLayout(view.context).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(16, 16, 16, 16)
                addView(taskTextView)
                addView(deadlineTextView)
                addView(finishButton)
            }
            (view as LinearLayout).addView(layout)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val itemView = LinearLayout(parent.context).apply {
            orientation = LinearLayout.VERTICAL
        }
        return TodoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todoItem = todoList[position]
        holder.taskTextView.text = todoItem.task
        holder.deadlineTextView.text = todoItem.deadline

        // Set up the Finish button click listener
        holder.finishButton.setOnClickListener {
            onFinish?.invoke(todoItem)
        }
    }

    override fun getItemCount(): Int = todoList.size
}
