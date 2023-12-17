package com.example.consistency

import HabitsAdapter
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HabitsAdapter
    private var habits: List<Habit> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerViewHabits)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val buttonLogHabits = findViewById<Button>(R.id.buttonLogHabits)
        buttonLogHabits.setOnClickListener {
            logSelectedHabits()
        }

        val buttonLogForSpecificDate = findViewById<Button>(R.id.buttonLogForSpecificDate)
        buttonLogForSpecificDate.setOnClickListener {
            showDatePicker()
        }

        fetchHabits()
    }
    private fun logSelectedHabits() {
        val selectedHabitNames = adapter.getSelectedHabitNames(adapter.getSelectedHabits())

        if (selectedHabitNames.isEmpty()) {
            Toast.makeText(this, "No habits selected", Toast.LENGTH_SHORT).show()
            return
        }

        // Format the current date
        val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val habitLogRequest = HabitLogRequest(selectedHabitNames, formattedDate)

        RetrofitClient.apiService.logHabits(habitLogRequest).enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful && response.body() != null) {
                    // Check if the response body contains any message indicating that the habit has already been logged
                    val responseBody = response.body()!!
                    if (responseBody.any { it.contains("already been logged") }) {
                        // If any habit has already been logged, show a different toast
                        Toast.makeText(this@MainActivity, responseBody.joinToString("\n"), Toast.LENGTH_LONG).show()
                    } else {
                        // If all habits are logged successfully without issues
                        Toast.makeText(this@MainActivity, "Habits logged successfully", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Error logging habits", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun logHabitsForDate(date: Date) {
        val selectedHabitNames = adapter.getSelectedHabitNames(adapter.getSelectedHabits())

        if (selectedHabitNames.isEmpty()) {
            Toast.makeText(this, "No habits selected", Toast.LENGTH_SHORT).show()
            return
        }

        // Format the date as required by your API
        val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
        val habitLogRequest = HabitLogRequest(selectedHabitNames, formattedDate)

        RetrofitClient.apiService.logPastHabits(habitLogRequest).enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(this@MainActivity, "Habits logged for date: $formattedDate", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Error logging habits for date", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "API call failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteHabit(habitId: String) {
        RetrofitClient.apiService.deleteHabit(habitId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("MainActivity", "Habit deleted successfully")
                } else {
                    Log.e("MainActivity", "Error deleting habit")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("MainActivity", "API call failed: ${t.message}")
            }
        })
    }

    private fun fetchHabits() {
        RetrofitClient.apiService.getAllHabits().enqueue(object : Callback<List<Habit>> {
            override fun onResponse(call: Call<List<Habit>>, response: Response<List<Habit>>) {
                if (response.isSuccessful && response.body() != null) {
                    habits = response.body()!! // Update the list here
                    adapter = HabitsAdapter(habits)
                    recyclerView.adapter = adapter
                } else {
                    Log.e("MainActivity", "Error: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<List<Habit>>, t: Throwable) {
                Log.e("MainActivity", "API call failed: ${t.message}")
            }
        })
    }

    private fun showDatePicker(){
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                logHabitsForDate(selectedDate.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
}