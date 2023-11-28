package com.example.consistency

import java.util.Date

data class HabitRecord(
    val recordId: String,
    val habitName: String,
    val recordDate: Date,
    val status: String
)