package com.codingblocks.cbonlineapp.mycourse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingblocks.cbonlineapp.util.SingleLiveEvent
import com.codingblocks.cbonlineapp.util.extensions.retrofitCallback
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.ProductExtensionsItem
import com.codingblocks.onlineapi.models.ResetRunAttempt
import kotlinx.coroutines.launch

class MyCourseViewModel(
    private val repository: MyCourseRepository
) : ViewModel() {

    var progress: MutableLiveData<Boolean> = MutableLiveData()
    var revoked: MutableLiveData<Boolean> = MutableLiveData()
    var attemptId: String = ""
    var runId: String = ""
    var courseId: String = ""
    private val mutablePopMessage = SingleLiveEvent<String>()
    private val extensions = MutableLiveData<List<ProductExtensionsItem>>()
    val popMessage: LiveData<String> = mutablePopMessage
    var resetProgress: MutableLiveData<Boolean> = MutableLiveData()

    fun getAllContent() = repository.getSectionWithContent(attemptId)

    fun getInstructor() = repository.getInstructorWithCourseId(courseId)

    fun getResumeCourse() = repository.resumeCourse(attemptId)

    fun getRun() = repository.run(runId)

    fun updatehit(attemptId: String) {
        repository.updateHit(attemptId)
    }

    fun fetchCourse(attemptId: String) {
        Clients.onlineV2JsonApi.enrolledCourseById(attemptId)
            .enqueue(retrofitCallback { _, response ->
                response?.let { runAttempt ->
                    if (runAttempt.isSuccessful) {
                        runAttempt.body()?.run?.sections?.let { sectionList ->
                            viewModelScope.launch {
                                repository.insertSections(sectionList, attemptId)
                            }
                            sectionList.forEach { courseSection ->
                                courseSection.courseContentLinks?.related?.href?.substring(7)
                                    ?.let { contentLink ->
                                        Clients.onlineV2JsonApi.getSectionContents(contentLink)
                                            .enqueue(retrofitCallback { _, contentResponse ->
                                                contentResponse?.let { contentList ->
                                                    if (contentList.isSuccessful) {
                                                        viewModelScope.launch {
                                                            contentList.body()?.let { repository.insertContents(it, attemptId, courseSection.id) }
                                                        }
                                                    }
                                                }
                                            })
                                    }
                            }
                        }
                        progress.value = false
                    } else if (runAttempt.code() == 404) {
                        revoked.value = true
                    }
                }
            })
    }

    fun resetProgress() {
        val resetCourse = ResetRunAttempt(attemptId)
        Clients.api.resetProgress(resetCourse).enqueue(retrofitCallback { _, response ->
            resetProgress.value = response?.isSuccessful ?: false
        })
    }

    fun requestApproval() {
        Clients.api.requestApproval(attemptId).enqueue(retrofitCallback { throwable, response ->
            response.let {
                if (it?.isSuccessful == true) {
                    mutablePopMessage.value = it.body()?.string()
                } else {
                    mutablePopMessage.value = it?.errorBody()?.string()
                }
            }
            throwable.let {
                mutablePopMessage.value = it?.message
            }
        })
    }

    fun fetchExtensions(productId: Int): MutableLiveData<List<ProductExtensionsItem>> {
        Clients.api.getExtensions(productId).enqueue(retrofitCallback { throwable, response ->
            response?.body().let { list ->
                if (response?.isSuccessful == true) {
                    extensions.postValue(list?.productExtensions)
                }
            }
            throwable.let {
                mutablePopMessage.value = it?.message
            }
        })
        return extensions
    }
}
