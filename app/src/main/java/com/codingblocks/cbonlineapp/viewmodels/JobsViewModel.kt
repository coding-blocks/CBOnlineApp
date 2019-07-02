package com.codingblocks.cbonlineapp.viewmodels

import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.database.JobsDao
import com.codingblocks.cbonlineapp.database.models.Companies
import com.codingblocks.cbonlineapp.database.models.JobsModel
import com.codingblocks.cbonlineapp.extensions.getDate
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.onlineapi.Clients

class JobsViewModel(
    private val jobsDao: JobsDao
) : ViewModel() {

    fun getJobs() {
        Clients.onlineV2JsonApi.getJobs(
            getDate(),
            getDate()
        ).enqueue(retrofitCallback { throwable, response ->
            response?.body().let {
                if (response?.isSuccessful == true) {
                    response.body()?.run {
                        forEach {

                            val job = JobsModel(
                                it.id,
                                it.coverImage,
                                it.ctc,
                                it.deadline,
                                it.description,
                                it.eligibility,
                                it.experience,
                                it.location,
                                it.postedOn,
                                it.type,
                                it.title,
                                company = with(it.company) {
                                    Companies(
                                        this?.id!!,
                                        name ?: "",
                                        logo ?: "",
                                        description ?: "",
                                        website ?: ""
                                    )
                                }

                            )
                            jobsDao.insertNew(job)
                        }
                    }
                }
            }
        })
    }

}
