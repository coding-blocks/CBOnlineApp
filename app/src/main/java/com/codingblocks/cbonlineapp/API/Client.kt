package com.codingblocks.cbonlineapp.API

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object Client {

    val retrofit = Retrofit.Builder()
            .baseUrl("https://account.codingblocks.com/api/users/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val api = retrofit.create(JsonPlaceholderApi::class.java)
}
