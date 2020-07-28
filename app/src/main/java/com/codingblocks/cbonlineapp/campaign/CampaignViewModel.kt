package com.codingblocks.cbonlineapp.campaign

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.cbonlineapp.util.extensions.savedStateValue
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.models.SpinResponse
import com.codingblocks.onlineapi.models.Spins
import kotlinx.coroutines.Dispatchers

const val SPINS_LEFT = "spinsLeft"
const val REFERRAL = "referral"

class CampaignViewModel(
    handle: SavedStateHandle,
    private val repo: CampaignRepository
) : BaseCBViewModel() {

    private var spinsLeft by savedStateValue<Int>(handle, SPINS_LEFT)
    var spinsLiveData = MutableLiveData<Int>(spinsLeft)
    var referral by savedStateValue<String>(handle, REFERRAL)
    var spinResponse: SpinResponse? = null

    var myWinnings = MutableLiveData<List<Spins>>()
    private var leaderBoard: LiveData<PagedList<Spins>>

    init {
        val config = PagedList.Config.Builder()
            .setPageSize(10)
            .setEnablePlaceholders(true)
            .build()
        leaderBoard = initializedPagedListBuilder(config).build()
        fetchReferralCode()
    }

    fun fetchSpins() {
        runIO {
            when (val response = repo.getSpinStats()) {
                is ResultWrapper.GenericError -> {
                    setError(response.error)
                    spinsLiveData.postValue(spinsLeft)
                }
                is ResultWrapper.Success -> with(response.value) {
                    if (response.value.isSuccessful)
                        response.value.body()?.let {
                            val availableSpins = it.get("availableSpins").asInt
                            spinsLeft = availableSpins
                            spinsLiveData.postValue(spinsLeft)
                        }
                    else {
                        setError(fetchError(response.value.code()))
                        spinsLiveData.postValue(spinsLeft)
                    }
                }
            }
        }
    }

    fun drawSpin() {
        runIO {
            when (val response = repo.drawSpin()) {
                is ResultWrapper.GenericError -> {
                    setError(response.error)
                }
                is ResultWrapper.Success -> with(response.value) {
                    if (response.value.isSuccessful)
                        response.value.body()?.let {
                            spinResponse = it
                        }
                    else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

    private fun fetchReferralCode() {
        runIO {
            when (val response = repo.getReferral()) {
                is ResultWrapper.GenericError -> {
                    setError(response.error)
                }
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        referral = body()?.get("code")?.asString
                    }
                }
            }
        }
    }

    fun fetchWinnings() {
        runIO {
            when (val response = repo.getMyWinnings()) {
                is ResultWrapper.GenericError -> {
                    myWinnings.postValue(emptyList())
                    setError(response.error)
                }
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        response.value.body()?.let {
                            myWinnings.postValue(it.get())
                        }
                    } else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

    fun fetchRules() = liveData(Dispatchers.IO) {
        emit(repo.getRules())
    }

    fun getLeaderBoard(): LiveData<PagedList<Spins>> = leaderBoard

    private fun initializedPagedListBuilder(config: PagedList.Config):
        LivePagedListBuilder<String, Spins> {

            val dataSourceFactory = object : DataSource.Factory<String, Spins>() {
                override fun create(): DataSource<String, Spins> {
                    return CampaignDataSource(viewModelScope)
                }
            }
            return LivePagedListBuilder(dataSourceFactory, config)
        }
}
