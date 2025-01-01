package com.ady.studybuddy5

data class TodoItem(
    val task: String,
    val description: String,
    val deadline: String,
    val isCompleted: Boolean = false
)
