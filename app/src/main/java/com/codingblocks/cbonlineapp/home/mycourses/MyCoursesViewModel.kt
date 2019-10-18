package com.codingblocks.cbonlineapp.home.mycourses

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Config
import androidx.paging.toLiveData
import com.codingblocks.cbonlineapp.util.extensions.retrofitCallback
import com.codingblocks.onlineapi.Clients
import com.crashlytics.android.Crashlytics
import kotlinx.coroutines.launch

class MyCoursesViewModel(
    private val repository: MyCoursesRepository
) : ViewModel() {
    var message: MutableLiveData<String> = MutableLiveData()
    var progress: MutableLiveData<Boolean> = MutableLiveData()

    fun getMyRuns() = repository.getMyRuns().toLiveData(Config(5, enablePlaceholders = false))

    fun getTopRun() = repository.getTopRun()

    fun fetchMyCourses(refresh: Boolean = false) {
        progress.postValue(true)
        Clients.onlineV2JsonApi.getMyCourses()
            .enqueue(retrofitCallback { error, res ->
                res?.let { response ->
                    if (response.isSuccessful) {
                        response.body()?.let { courseList ->
                            courseList.forEach { courseRun ->
                                courseRun.runAttempts?.get(0)?.id?.let { runId ->
                                    Clients.api.getMyCourseProgress(runId)
                                        .enqueue(retrofitCallback { _, progressRes ->
                                            progressRes?.body().let {
                                                var progress = 0.0
                                                var completedContents = 0.0
                                                var totalContents = 0.0
                                                try {
                                                    progress = it?.get("percent") as Double
                                                    completedContents = it["completedContents"] as Double
                                                    totalContents = it["totalContents"] as Double
                                                } catch (e: Exception) {
                                                    Crashlytics.logException(e)
                                                }

                                                viewModelScope.launch {
                                                    courseRun.course?.let { it1 -> repository.insertCourse(it1) }
                                                    repository.insertRun(courseRun,
                                                        progress,
                                                        totalContents.toInt(),
                                                        completedContents.toInt(),
                                                        refresh)
                                                }

                                                courseRun.course?.instructors?.forEach { instructorId ->
                                                    Clients.onlineV2JsonApi.instructorsById(
                                                        instructorId.id
                                                    ).enqueue(
                                                        retrofitCallback { _, instructorResponse ->
                                                            instructorResponse?.let {
                                                                if (instructorResponse.isSuccessful) {
                                                                    instructorResponse.body()?.run {
                                                                        viewModelScope.launch {
                                                                            courseRun.course?.id?.let { it1
                                                                                ->
                                                                                repository.insertInstructor(this@run, it1)
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        })
                                                }
                                            }
                                        })
                                }
                            }
                        }
                        progress.value = false
                    }
                }
                error?.let {
                    message.postValue(it.localizedMessage)
                    progress.value = false
                }
            })
    }
}
