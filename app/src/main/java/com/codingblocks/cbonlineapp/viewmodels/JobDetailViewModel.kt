package com.codingblocks.cbonlineapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.database.CourseDao
import com.codingblocks.cbonlineapp.database.CourseRunDao
import com.codingblocks.cbonlineapp.database.CourseWithInstructorDao
import com.codingblocks.cbonlineapp.database.InstructorDao
import com.codingblocks.cbonlineapp.database.JobsDao
import com.codingblocks.cbonlineapp.database.models.Companies
import com.codingblocks.cbonlineapp.database.models.JobsModel
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Form

class JobDetailViewModel(
    private val jobsDao: JobsDao,
    private val courseWithInstructorDao: CourseWithInstructorDao,
    private val courseDao: CourseDao,
    private val runDao: CourseRunDao,
    private val instructorDao: InstructorDao
) : ViewModel() {

    val eligibleLiveData: MutableLiveData<Boolean> = MutableLiveData()

    val inactiveLiveData: MutableLiveData<Boolean> = MutableLiveData()

    val acceptingLiveData: MutableLiveData<Boolean> = MutableLiveData()

    val formData: MutableLiveData<ArrayList<Form>> = MutableLiveData()

    fun getAllJobs() = jobsDao.getAllJobs()

    fun getJobs(jobId: String) {
        Clients.onlineV2JsonApi.getJobById(
            jobId
        ).enqueue(retrofitCallback { throwable, response ->
            response?.body().let {
                if (response?.isSuccessful == true) {
                    response.body()?.run {
                        eligibleLiveData.value = eligible
                        acceptingLiveData.value = accepting
                        formData.value = form
                        val job = JobsModel(
                            id,
                            coverImage,
                            ctc,
                            deadline,
                            description,
                            eligibility,
                            experience,
                            location,
                            postedOn,
                            type,
                            title,
                            with(company!!) {
                                inactiveLiveData.value = inactive
                                Companies(
                                    id,
                                    name ?: "",
                                    logo ?: "",
                                    description ?: "",
                                    website ?: ""
                                )
                            },
                            courses ?: arrayListOf()
                        )

                        jobsDao.insertNew(job)
                    }
                }
            }
        })
    }
}

