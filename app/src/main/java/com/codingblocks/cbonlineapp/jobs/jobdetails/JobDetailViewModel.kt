package com.codingblocks.cbonlineapp.jobs.jobdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.codingblocks.cbonlineapp.CBOnlineApp.Companion.mInstance
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import com.codingblocks.cbonlineapp.database.CourseWithInstructorDao
import com.codingblocks.cbonlineapp.database.JobsDao
import com.codingblocks.cbonlineapp.database.models.Companies
import com.codingblocks.cbonlineapp.database.models.CourseInstructorPair
import com.codingblocks.cbonlineapp.database.models.JobsModel
import com.codingblocks.cbonlineapp.util.extensions.retrofitCallback
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Applications
import com.codingblocks.onlineapi.models.Course
import com.codingblocks.onlineapi.models.Form
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class JobDetailViewModel(
    private val jobsDao: JobsDao,
    private val courseWithInstructorDao: CourseWithInstructorDao
) : BaseCBViewModel() {

    val eligibleLiveData: MutableLiveData<String> = MutableLiveData()
    private val inactiveLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val acceptingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val formData: MutableLiveData<ArrayList<Form>> = MutableLiveData()
    val courseIdList: MutableLiveData<ArrayList<Course>> = MutableLiveData()
    var jobCourses: LiveData<List<CourseInstructorPair>> = MutableLiveData()

//    init {
//        jobCourses = Transformations.switchMap(courseIdList) { list ->
//            val coureidlist = ArrayList<String>()
//            list.forEach {
//                it.id?.let { it1 -> coureidlist.add(it1) }
//            }
// //            courseWithInstructorDao.getJobCourses(coureidlist)
//        }
//    }

    fun getJobById(id: String) = jobsDao.getJobById(id)

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
                            courses ?: arrayListOf<Course>()
                        )
                        if (application != null) {
                            eligibleLiveData.value = mInstance.getString(R.string.applied)
                        }
                        viewModelScope.launch(Dispatchers.IO) {
                            jobsDao.insert(job)
                        }
                    }
                }
            }
        })
    }

    fun applyJob(application: Applications) {
        Clients.onlineV2JsonApi.applyJob(application).enqueue(retrofitCallback { throwable, response ->
            if (response?.isSuccessful == true) {
                eligibleLiveData.value = mInstance.getString(R.string.applied)
            }
        })
    }
}
