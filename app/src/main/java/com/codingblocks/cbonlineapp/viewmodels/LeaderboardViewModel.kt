package com.codingblocks.cbonlineapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Leaderboard

class LeaderboardViewModel : ViewModel() {

    var leaderboard: MutableLiveData<List<Leaderboard>> = MutableLiveData()

    fun getLeaderboard(runId: String) {
        Clients.api.leaderboardById(runId).enqueue(retrofitCallback { throwable, response ->
            leaderboard.value = response?.body()
        })
    }
}
