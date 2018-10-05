package com.codingblocks.cbonlineapp

import androidx.annotation.Nullable
import com.google.gson.annotations.SerializedName

data class User(val id: Int, val username: String, val firstname: String, val lastname: String, val photo: String, val email: String, val createdAt: String, val updatedAt: String)


data class RatingModel(
        val rating: String,
        val count: Int,
        val stats: List<Double>,
        val userScore: Any?
)