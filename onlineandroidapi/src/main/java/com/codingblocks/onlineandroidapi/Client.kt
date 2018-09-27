package com.codingblocks.onlineandroidapi


import com.codingblocks.onlineandroidapi.api.AuthApi
import com.codingblocks.onlineandroidapi.api.OnlineApi
import com.codingblocks.onlineandroidapi.api.QbountyApi
import com.codingblocks.onlineandroidapi.models.qbounty.Claim
import com.codingblocks.onlineandroidapi.models.qbounty.Task
import com.codingblocks.onlineandroidapi.models.qbounty.User
import com.github.jasminb.jsonapi.ResourceConverter
import com.github.jasminb.jsonapi.retrofit.JSONAPIConverterFactory
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

    val qbountyApi = Retrofit.Builder()
            .baseUrl("https://cb-qbounty.herokuapp.com/api/")
            .addConverterFactory(JSONAPIConverterFactory(ResourceConverter(
                    User::class.java,
                    Task::class.java,
                    Claim::class.java
            )))
            .build()
            .create(QbountyApi::class.java)


}
