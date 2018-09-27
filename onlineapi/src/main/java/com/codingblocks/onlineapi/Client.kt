package com.codingblocks.onlineapi


import com.codingblocks.onlineapi.api.AuthApi
import com.codingblocks.onlineapi.api.OnlineApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object Client {

    val retrofitAuth = Retrofit.Builder()
            .baseUrl("https://account.codingblocks.com/apiAuth/users/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val apiAuth = retrofitAuth.create(AuthApi::class.java)

    val retrofit = Retrofit.Builder()
            .baseUrl("https://api-online.cb.lk/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val api = retrofit.create(OnlineApi::class.java)

}
