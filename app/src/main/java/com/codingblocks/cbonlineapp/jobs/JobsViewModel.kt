package com.codingblocks.cbonlineapp.jobs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import com.codingblocks.cbonlineapp.database.models.JobsModel
import com.codingblocks.cbonlineapp.jobs.jobdetails.JobRepository
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError

class JobsViewModel(
    private val repo: JobRepository
) : BaseCBViewModel() {
    var type: MutableLiveData<String> = MutableLiveData()
//
//    var searchFilters = FilterData()
//    val allJobList = mutableListOf<JobsModel>()
//
//    var jobProgress = MutableLiveData<Boolean>(false)
//    var fetchError = MutableLiveData<Boolean>(false)
//
//    var noFilteredJobs = false

//    fun getAllJobs() = jobsDao.getAllJobs()
// var filteredJobsProgress: MutableLiveData<Boolean> = MutableLiveData(false)

    fun getJobs(): LiveData<List<JobsModel>> {
        runIO {
            when (val response = repo.fetchJobs()) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        response.value.body()?.let { jobs ->
                            repo.insertJobs(jobs.get() ?: emptyList())
                        }
                    else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
        return repo.getJobs()
    }

    fun getFilteredList() {
//        allJobList.clear()
//        filteredJobsProgress.value = true
//        Clients.onlineV2JsonApi.getJobs(
//            getDate(),
//            getDate(),
//            searchFilters.filterLocation,
//            searchFilters.filterJobtype
//        ).enqueue(retrofitCallback { _, response ->
//            response?.body().let {
//                if (response?.isSuccessful == true) {
//                    response.body()?.run {
//                        if (this.isEmpty()) {
//                            noFilteredJobs = true
//                            filteredJobsProgress.value = false
//                        }
//                        forEachIndexed { index, job ->
//                            noFilteredJobs = false
//                            Clients.onlineV2JsonApi.getCompany(job.company?.id ?: "").enqueue(
//                                retrofitCallback { _, response ->
//                                    response?.body()?.let {
//                                        allJobList.add(JobsModel(
//                                            job.id,
//                                            job.coverImage,
//                                            job.ctc,
//                                            job.deadline,
//                                            job.description,
//                                            job.eligibility,
//                                            job.experience,
//                                            job.location,
//                                            job.postedOn,
//                                            job.type,
//                                            job.title,
//                                            with(it) {
//                                                Companies(
//                                                    id,
//                                                    name ?: "",
//                                                    logo ?: "",
//                                                    description ?: "",
//                                                    website ?: ""
//                                                )
//                                            },
//                                            job.courses ?: arrayListOf()
//                                        ))
//                                    }
//
//                                    if (index == this.size - 1) {
//                                        filteredJobsProgress.value = false
//                                    }
//                                })
//                        }
//                    }
//                }
//            }
//        })
    }
}
