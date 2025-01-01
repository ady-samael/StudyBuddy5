package com.ady.studybuddy5

import android.content.Context
import android.content.SharedPreferences

class SchedulePreferences(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("schedule_prefs", Context.MODE_PRIVATE)

    // Key for storing the schedules
    private val SCHEDULES_KEY = "schedules"

    // Function to save the list of schedules to SharedPreferences as a string
    fun saveSchedules(schedules: List<ClassSchedule>) {
        val scheduleStrings = schedules.joinToString(separator = "\n") { schedule ->
            "${schedule.courseName},${schedule.professorName},${schedule.time}"
        }
        sharedPreferences.edit().putString(SCHEDULES_KEY, scheduleStrings).apply()
    }

    // Function to load the list of schedules from SharedPreferences
    fun loadSchedules(): List<ClassSchedule> {
        val scheduleStrings = sharedPreferences.getString(SCHEDULES_KEY, null)
        val scheduleList = mutableListOf<ClassSchedule>()
        scheduleStrings?.let {
            val lines = it.split("\n")
            for (line in lines) {
                val parts = line.split(",")
                if (parts.size == 3) {
                    val schedule = ClassSchedule(parts[0], parts[1], parts[2])
                    scheduleList.add(schedule)
                }
            }
        }
        return scheduleList
    }
}
