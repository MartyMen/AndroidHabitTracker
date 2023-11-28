package com.example.consistency

import HabitsAdapter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HabitsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerViewHabits)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val buttonLogHabits = findViewById<Button>(R.id.buttonLogHabits)
        buttonLogHabits.setOnClickListener {
            logSelectedHabits()
        }

        fetchHabits()
    }

    private fun logSelectedHabits() {
        // Assuming HabitsAdapter has a method getSelectedHabits() that returns a list of selected Habit IDs
        val selectedHabits = adapter.getSelectedHabits()
        selectedHabits.forEach { habitId ->
            // Assuming each Habit object has an ID and a name
            RetrofitClient.apiService.logHabit(Habit(habitId, "Habit Name")).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("MainActivity", "Habit logged successfully")
                    } else {
                        Log.e("MainActivity", "Error logging habit")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("MainActivity", "API call failed: ${t.message}")
                }
            })
        }
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
                    adapter = HabitsAdapter(response.body()!!)
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
}
