package com.codingblocks.cbonlineapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.database.CourseRunDao
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Leaderboard
import org.jetbrains.anko.doAsync
import kotlin.concurrent.thread

class LeaderboardViewModel(var courseDao: CourseRunDao) : ViewModel() {

    var leaderboard: MutableLiveData<List<Leaderboard>> = MutableLiveData()

    fun getLeaderboard(attemptId: String) {
        thread {
            doAsync {
                val courseRun = courseDao.getRunById(attemptId)
                val runId = courseRun.crUid
                Clients.api.leaderboardById(runId).enqueue(retrofitCallback { throwable, response ->
                    leaderboard.value = response?.body()
                })
            }
        }
    }

}
