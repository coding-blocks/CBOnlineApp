package com.codingblocks.onlineandroidapi.api

import com.codingblocks.onlineandroidapi.models.qbounty.Claim
import com.codingblocks.onlineandroidapi.models.qbounty.Task
import com.codingblocks.onlineandroidapi.models.qbounty.User
import retrofit2.Call
import retrofit2.http.GET

interface QbountyApi {
    @get:GET("tasks")
    val tasks: Call<ArrayList<Task>>

    @get:GET("claims?include=claimant")
    val claims: Call<ArrayList<Claim>>

    @get:GET("users")
    val users: Call<ArrayList<User>>
}