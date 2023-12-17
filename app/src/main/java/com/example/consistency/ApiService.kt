package com.example.consistency

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @GET("habits")
    fun getAllHabits(): Call<List<Habit>>

    @GET("habits/date/{date}")
    fun getHabitsByDate(@Path("date") date: String): Call<List<Habit>>

    @GET("habitrecords/{habitName}")
    fun getHabitRecords(@Path("habitName") habitName: String): Call<List<HabitRecord>>

    @POST("habits")
    fun logHabit(@Body habit: Habit): Call<Void>

    @DELETE("habits/{habitName}")
    fun deleteHabit(@Path("habitName") habitName: String): Call<Void>

    @POST("habitrecords")
    fun logHabits(@Body habitLogRequest: HabitLogRequest): Call<List<String>>

    @POST("habitrecords/log-past-habits")
    fun logPastHabits(@Body habitLogRequest: HabitLogRequest): Call<List<String>>

}


//TODO Add functionality to log multiple habits on previous days. ALSO Add functionality to delete records