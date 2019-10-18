package com.codingblocks.cbonlineapp.jobs.jobdetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingblocks.cbonlineapp.CBOnlineApp.Companion.mInstance
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.database.CourseDao
import com.codingblocks.cbonlineapp.database.CourseRunDao
import com.codingblocks.cbonlineapp.database.CourseWithInstructorDao
import com.codingblocks.cbonlineapp.database.JobsDao
import com.codingblocks.cbonlineapp.database.models.Companies
import com.codingblocks.cbonlineapp.database.models.JobsModel
import com.codingblocks.cbonlineapp.database.models.RunModel
import com.codingblocks.cbonlineapp.util.extensions.retrofitCallback
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Applications
import com.codingblocks.onlineapi.models.CourseId
import com.codingblocks.onlineapi.models.Form
import kotlinx.coroutines.launch

class JobDetailViewModel(
    private val jobsDao: JobsDao,
    private val courseWithInstructorDao: CourseWithInstructorDao,
    private val courseDao: CourseDao,
    private val runDao: CourseRunDao
) : ViewModel() {

    val eligibleLiveData: MutableLiveData<String> = MutableLiveData()

    val inactiveLiveData: MutableLiveData<Boolean> = MutableLiveData()

    val acceptingLiveData: MutableLiveData<Boolean> = MutableLiveData()

    val formData: MutableLiveData<ArrayList<Form>> = MutableLiveData()

    val jobCourses: MutableLiveData<List<RunModel>> = MutableLiveData()

    fun getJobById(id: String) = jobsDao.getJobById(id)

    fun getCourseDao() = courseDao

    fun getCourseWithInstructorDao() = courseWithInstructorDao

    fun fetchJob(jobId: String) {
        Clients.onlineV2JsonApi.getJobById(
            jobId
        ).enqueue(retrofitCallback { throwable, response ->
            response?.body().let {
                if (response?.isSuccessful == true) {
                    response.body()?.run {
                        eligibleLiveData.value = if (eligible) "eligible" else "not eligible"
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
                        if (application != null) {
                            eligibleLiveData.value = mInstance.getString(R.string.applied)
                        }
                        viewModelScope.launch {
                            jobsDao.insert(job)
                        }
                    }
                }
            }
        })
    }

    fun getCourses(courseId: ArrayList<CourseId>) {
        val coureidlist = ArrayList<String>()
        courseId.forEach {
            it.id?.let { it1 -> coureidlist.add(it1) }
        }
        jobCourses.value = runDao.getJobCourses(coureidlist)
    }

    fun applyJob(application: Applications) {
        Clients.onlineV2JsonApi.applyJob(application).enqueue(retrofitCallback { throwable, response ->
            if (response?.isSuccessful == true) {
                eligibleLiveData.value = mInstance.getString(R.string.applied)
            }
        })
    }
}
