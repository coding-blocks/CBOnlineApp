package com.codingblocks.cbonlineapp.jobs

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.database.JobsDao
import com.codingblocks.cbonlineapp.database.models.Companies
import com.codingblocks.cbonlineapp.database.models.FilterData
import com.codingblocks.cbonlineapp.database.models.JobsModel
import com.codingblocks.cbonlineapp.util.extensions.getDate
import com.codingblocks.cbonlineapp.util.extensions.retrofitCallback
import com.codingblocks.onlineapi.Clients

class JobsViewModel(
    private val jobsDao: JobsDao
) : ViewModel() {

    var searchFilters = FilterData()
    val allJobList = mutableListOf<JobsModel>()

    fun getAllJobs() = jobsDao.getAllJobs()

    fun getJobs() {
        Clients.onlineV2JsonApi.getJobs(
            getDate(),
            getDate()
        ).enqueue(retrofitCallback { _, response ->
            response?.body().let {
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

                                        jobsDao.insertNew(job)
                                    }
                                })
                        }
                    }
                }
            }
        })
    }

    var filteredJobsProgress: MutableLiveData<Boolean> = MutableLiveData()

    fun getFilteredList() {
        allJobList.clear()
        Clients.onlineV2JsonApi.getJobs(
            getDate(),
            getDate(),
            searchFilters.filterLocation,
            searchFilters.filterJobtype
        ).enqueue(retrofitCallback { _, response ->
            response?.body().let {
                if (response?.isSuccessful == true) {
                    response.body()?.run {
                        forEachIndexed { index, job ->
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

                                        if (index == this.size - 1)
                                            filteredJobsProgress.value = true
                                    }
                                })
                        }
                    }
                }
            }
        })
    }
}
