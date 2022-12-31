package com.echadworks.meal.network

import com.echadworks.meal.model.Plan
import retrofit2.Call
import retrofit2.http.*

interface MealApi {
@GET("/mealPlan")
fun getMealPlan(): Call<List<Plan>>
}