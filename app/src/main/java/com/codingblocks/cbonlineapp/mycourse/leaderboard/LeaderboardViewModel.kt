package com.codingblocks.cbonlineapp.mycourse.leaderboard

import androidx.lifecycle.MutableLiveData
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import com.codingblocks.onlineapi.models.Leaderboard

class LeaderboardViewModel : BaseCBViewModel() {

    var leaderboard: MutableLiveData<List<Leaderboard>> = MutableLiveData()
}
