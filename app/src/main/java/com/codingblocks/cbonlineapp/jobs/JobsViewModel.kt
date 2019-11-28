package com.codingblocks.cbonlineapp.jobs

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingblocks.cbonlineapp.database.JobsDao
import com.codingblocks.cbonlineapp.database.models.Companies
import com.codingblocks.cbonlineapp.database.models.FilterData
import com.codingblocks.cbonlineapp.database.models.JobsModel
import com.codingblocks.cbonlineapp.util.extensions.getDate
import com.codingblocks.cbonlineapp.util.extensions.retrofitCallback
import com.codingblocks.onlineapi.Clients
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class JobsViewModel(
    private val jobsDao: JobsDao
) : ViewModel() {

    var searchFilters = FilterData()
    val allJobList = mutableListOf<JobsModel>()

    var jobProgress = MutableLiveData<Boolean>(false)
    var fetchError = MutableLiveData<Boolean>(false)

    var noFilteredJobs = false

    fun getAllJobs() = jobsDao.getAllJobs()

    fun getJobs() {
        jobProgress.value = true
        Clients.onlineV2JsonApi.getJobs(
            getDate(),
            getDate()
        ).enqueue(retrofitCallback { t, response ->
            response?.body().let {
                fetchError.value = false
                if (response?.isSuccessful == true) {
                    response.body()?.run {
                        forEach { job ->

                            Clients.onlineV2JsonApi.getCompany(job.company?.id ?: "").enqueue(
                                retrofitCallback { _, response ->
                                    response?.body()?.let {
                                        val job = JobsModel(
                                            job.id,
                                            job.coverImage,
                                            job.ctc,
                                            job.deadline,
                                            job.description,
                                            job.eligibility,
                                            job.experience,
                                            job.location,
                                            job.postedOn,
                                            job.type,
                                            job.title,
                                            with(it) {
                                                Companies(
                                                    id,
                                                    name ?: "",
                                                    logo ?: "",
                                                    description ?: "",
                                                    website ?: ""
                                                )
                                            },
                                            job.courses ?: arrayListOf()
                                        )
                                        viewModelScope.launch(Dispatchers.IO) {
                                            jobsDao.insert(job)
                                        }
                                    }
                                })
                        }
                        jobProgress.value = false
                    }
                }
            }

            t?.let {
                fetchError.value = true
            }
        })
    }

    var filteredJobsProgress: MutableLiveData<Boolean> = MutableLiveData(false)

    fun getFilteredList() {
        allJobList.clear()
        filteredJobsProgress.value = true
        Clients.onlineV2JsonApi.getJobs(
            getDate(),
            getDate(),
            searchFilters.filterLocation,
            searchFilters.filterJobtype
        ).enqueue(retrofitCallback { _, response ->
            response?.body().let {
                if (response?.isSuccessful == true) {
                    response.body()?.run {
                        if (this.isEmpty()) {
                            noFilteredJobs = true
                            filteredJobsProgress.value = false
                        }
                        forEachIndexed { index, job ->
                            noFilteredJobs = false
                            Clients.onlineV2JsonApi.getCompany(job.company?.id ?: "").enqueue(
                                retrofitCallback { _, response ->
                                    response?.body()?.let {
                                        allJobList.add(JobsModel(
                                            job.id,
                                            job.coverImage,
                                            job.ctc,
                                            job.deadline,
                                            job.description,
                                            job.eligibility,
                                            job.experience,
                                            job.location,
                                            job.postedOn,
                                            job.type,
                                            job.title,
                                            with(it) {
                                                Companies(
                                                    id,
                                                    name ?: "",
                                                    logo ?: "",
                                                    description ?: "",
                                                    website ?: ""
                                                )
                                            },
                                            job.courses ?: arrayListOf()
                                        ))
                                    }

                                    if (index == this.size - 1) {
                                        filteredJobsProgress.value = false
                                    }
                                })
                        }
                    }
                }
            }
        })
    }
}
