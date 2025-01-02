package com.ady.studybuddy5

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Data class to represent a class schedule with start and end times
data class ClassSchedule(
    val courseName: String,
    val professorName: String,
    val startTime: String,  // Start time of the class
    val endTime: String     // End time of the class
)

class ScheduleAdapter(
    private val scheduleList: MutableList<ClassSchedule>, // Mutable list for updates
    private val deleteTimeSlot: (Int) -> Unit             // Function to delete an item
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    // ViewHolder class to represent each item view in the RecyclerView
    inner class ScheduleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val courseNameTextView: TextView
        val professorNameTextView: TextView
        val timeTextView: TextView
        val deleteButton: TextView // Button for deleting the schedule item

        init {
            // Initialize the views in the item layout
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

    // Create a new view holder for each schedule item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        // Inflate the layout for the schedule item
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.schedule_item, parent, false)
        return ScheduleViewHolder(itemView)
    }

    // Bind data to the views in the view holder
    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val scheduleItem = scheduleList[position]
        // Set the course name, professor name, and time to the corresponding TextViews
        holder.courseNameTextView.text = scheduleItem.courseName
        holder.professorNameTextView.text = "Professor: ${scheduleItem.professorName}"
        holder.timeTextView.text = "${scheduleItem.startTime} - ${scheduleItem.endTime}"
    }

    // Return the total number of items in the list
    override fun getItemCount(): Int {
        return scheduleList.size
    }
}
