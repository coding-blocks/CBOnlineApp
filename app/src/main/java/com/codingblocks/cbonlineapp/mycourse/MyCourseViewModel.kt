package com.codingblocks.cbonlineapp.mycourse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingblocks.cbonlineapp.database.models.ContentCodeChallenge
import com.codingblocks.cbonlineapp.database.models.ContentCsvModel
import com.codingblocks.cbonlineapp.database.models.ContentDocument
import com.codingblocks.cbonlineapp.database.models.ContentLecture
import com.codingblocks.cbonlineapp.database.models.ContentModel
import com.codingblocks.cbonlineapp.database.models.ContentQna
import com.codingblocks.cbonlineapp.database.models.ContentVideo
import com.codingblocks.cbonlineapp.database.models.SectionContentHolder
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
//                                                        repository.insertContents(contentList.body(),attemptId)
                                                        contentList.body()
                                                            ?.forEach { content ->
                                                                var contentDocument =
                                                                    ContentDocument()
                                                                var contentLecture =
                                                                    ContentLecture()
                                                                var contentVideo =
                                                                    ContentVideo()
                                                                var contentQna =
                                                                    ContentQna()
                                                                var contentCodeChallenge =
                                                                    ContentCodeChallenge()
                                                                var contentCsv =
                                                                    ContentCsvModel()

                                                                when {
                                                                    content.contentable == "lecture" -> content.lecture?.let { contentLectureType ->
                                                                        contentLecture =
                                                                            ContentLecture(
                                                                                contentLectureType.id,
                                                                                contentLectureType.name
                                                                                    ?: "",
                                                                                contentLectureType.duration
                                                                                    ?: 0,
                                                                                contentLectureType.videoId
                                                                                    ?: "",
                                                                                content.sectionContent?.id
                                                                                    ?: "",
                                                                                contentLectureType.updatedAt
                                                                            )
                                                                    }
                                                                    content.contentable == "document" -> content.document?.let { contentDocumentType ->
                                                                        contentDocument =
                                                                            ContentDocument(
                                                                                contentDocumentType.id,
                                                                                contentDocumentType.name
                                                                                    ?: "",
                                                                                contentDocumentType.pdfLink
                                                                                    ?: "",
                                                                                content.sectionContent?.id
                                                                                    ?: "",
                                                                                contentDocumentType.updatedAt
                                                                            )
                                                                    }
                                                                    content.contentable == "video" -> content.video?.let { contentVideoType ->
                                                                        contentVideo =
                                                                            ContentVideo(
                                                                                contentVideoType.id,
                                                                                contentVideoType.name
                                                                                    ?: "",
                                                                                contentVideoType.duration
                                                                                    ?: 0L,
                                                                                contentVideoType.description
                                                                                    ?: "",
                                                                                contentVideoType.url
                                                                                    ?: "",
                                                                                content.sectionContent?.id
                                                                                    ?: "",
                                                                                contentVideoType.updatedAt
                                                                            )
                                                                    }
                                                                    content.contentable == "qna" -> content.qna?.let { contentQna1 ->
                                                                        contentQna =
                                                                            ContentQna(
                                                                                contentQna1.id,
                                                                                contentQna1.name
                                                                                    ?: "",
                                                                                contentQna1.qId
                                                                                    ?: 0,
                                                                                content.sectionContent?.id
                                                                                    ?: "",
                                                                                contentQna1.updatedAt
                                                                            )
                                                                    }
                                                                    content.contentable == "code_challenge" -> content.codeChallenge?.let { codeChallenge ->
                                                                        contentCodeChallenge =
                                                                            ContentCodeChallenge(
                                                                                codeChallenge.id,
                                                                                codeChallenge.name
                                                                                    ?: "",
                                                                                codeChallenge.hbProblemId
                                                                                    ?: 0,
                                                                                codeChallenge.hbContestId
                                                                                    ?: 0,
                                                                                content.sectionContent?.id
                                                                                    ?: "",
                                                                                codeChallenge.updatedAt
                                                                            )
                                                                    }
                                                                    content.contentable == "csv" -> content.csv?.let {
                                                                        contentCsv =
                                                                            ContentCsvModel(
                                                                                it.id,
                                                                                it.name
                                                                                    ?: "",
                                                                                it.description
                                                                                    ?: "",
                                                                                it.contentId
                                                                                    ?: "",
                                                                                it.updatedAt
                                                                            )
                                                                    }
                                                                }

                                                                var progressId = ""
                                                                val status: String
                                                                if (content.progress != null) {
                                                                    status =
                                                                        content.progress?.status
                                                                            ?: ""
                                                                    progressId =
                                                                        content.progress?.id
                                                                            ?: ""
                                                                } else {
                                                                    status =
                                                                        "UNDONE"
                                                                }

                                                                val newContent =
                                                                    ContentModel(
                                                                        content.id,
                                                                        status,
                                                                        progressId,
                                                                        content.title,
                                                                        content.duration
                                                                            ?: 0,
                                                                        content.contentable,
                                                                        content.sectionContent?.order
                                                                            ?: 0,
                                                                        attemptId,
                                                                        contentLecture,
                                                                        contentDocument,
                                                                        contentVideo,
                                                                        contentQna,
                                                                        contentCodeChallenge,
                                                                        contentCsv
                                                                    )

                                                                val oldContent =
                                                                    contentsDao.getContentWithId(
                                                                        attemptId,
                                                                        content.id
                                                                    )
                                                                if (oldContent == null) {

                                                                    contentsDao.insert(
                                                                        newContent
                                                                    )
                                                                    sectionWithContentsDao.insert(
                                                                        SectionContentHolder.SectionWithContent(
                                                                            courseSection.id,
                                                                            content.id,
                                                                            content.sectionContent?.order
                                                                                ?: 0

                                                                        )
                                                                    )
                                                                } else if (oldContent != newContent) {
                                                                    contentLecture.isDownloaded =
                                                                        oldContent.contentLecture.isDownloaded
                                                                    contentsDao.update(
                                                                        newContent
                                                                    )
                                                                }
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
