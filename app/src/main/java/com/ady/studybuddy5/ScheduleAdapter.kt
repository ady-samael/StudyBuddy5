package com.ady.studybuddy5

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class ClassSchedule(
    val courseName: String,
    val professorName: String,  // Added professor name
    val time: String
)

class ScheduleAdapter(
    private val scheduleList: MutableList<ClassSchedule>, // Make the list mutable for changes
    private val deleteTimeSlot: (Int) -> Unit             // Function to delete an item
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    // ViewHolder class
    inner class ScheduleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val courseNameTextView: TextView
        val professorNameTextView: TextView
        val timeTextView: TextView
        val deleteButton: TextView // Button for deleting the schedule item

        init {
            // Correctly initialize the view here, no casting needed
            courseNameTextView = view.findViewById(R.id.courseNameTextView)
            professorNameTextView = view.findViewById(R.id.professorNameTextView)
            timeTextView = view.findViewById(R.id.timeTextView)
            deleteButton = view.findViewById(R.id.deleteButton)

            // Set up the delete button's onClickListener
            deleteButton.setOnClickListener {
                deleteTimeSlot(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        // Inflate a properly defined layout for each item in the RecyclerView
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.schedule_item, parent, false)
        return ScheduleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val scheduleItem = scheduleList[position]
        holder.courseNameTextView.text = scheduleItem.courseName
        holder.professorNameTextView.text = "Professor: ${scheduleItem.professorName}"
        holder.timeTextView.text = scheduleItem.time
    }

    override fun getItemCount(): Int {
        return scheduleList.size
    }
}
