package com.example.mms.model


data class CalendarDay(
    val date: String,
    val dayOfMonth: Int,
    val listTasks: List<Task>,
    var isSelected: Boolean = false
)
