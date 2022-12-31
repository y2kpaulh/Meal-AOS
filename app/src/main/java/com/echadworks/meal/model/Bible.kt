package com.echadworks.meal.model
import com.google.gson.annotations.SerializedName

class Bible : ArrayList<Bible.Book>(){
    data class Book(
        @SerializedName("abbrev")
        val abbrev: String,
        @SerializedName("chapters")
        val chapters: List<List<String>>,
        @SerializedName("name")
        val name: String
    )
}