package com.codingblocks.cbonlineapp.mycourse

import com.codingblocks.cbonlineapp.database.BookmarkDao
import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.database.CourseWithInstructorDao
import com.codingblocks.cbonlineapp.database.RunPerformanceDao
import com.codingblocks.cbonlineapp.database.SectionDao
import com.codingblocks.cbonlineapp.database.SectionWithContentsDao
import com.codingblocks.cbonlineapp.database.models.BookmarkModel
import com.codingblocks.cbonlineapp.database.models.ContentCodeChallenge
import com.codingblocks.cbonlineapp.database.models.ContentCsvModel
import com.codingblocks.cbonlineapp.database.models.ContentDocument
import com.codingblocks.cbonlineapp.database.models.ContentLecture
import com.codingblocks.cbonlineapp.database.models.ContentModel
import com.codingblocks.cbonlineapp.database.models.ContentQnaModel
import com.codingblocks.cbonlineapp.database.models.ContentVideo
import com.codingblocks.cbonlineapp.database.models.RunPerformance
import com.codingblocks.cbonlineapp.database.models.SectionContentHolder
import com.codingblocks.cbonlineapp.database.models.SectionModel
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.models.LectureContent
import com.codingblocks.onlineapi.models.PerformanceResponse
import com.codingblocks.onlineapi.models.ResetRunAttempt
import com.codingblocks.onlineapi.models.RunAttempts
import com.codingblocks.onlineapi.safeApiCall

class MyCourseRepository(
    private val sectionWithContentsDao: SectionWithContentsDao,
    private val contentsDao: ContentDao,
    private val sectionDao: SectionDao,
    private val courseWithInstructorDao: CourseWithInstructorDao,
    private val runPerformanceDao: RunPerformanceDao,
    private val bookmarkDao: BookmarkDao
) {

    fun getSectionWithContent(attemptId: String) = sectionWithContentsDao.getSectionWithContent(attemptId)

    suspend fun getSectionWithContentNonLive(attemptId: String) = sectionWithContentsDao.getSectionWithContentNonLive(attemptId)

    suspend fun insertSections(runAttempt: RunAttempts) {
        runAttempt.run?.sections?.forEach { courseSection ->
            courseSection.run {
                val newSection = SectionModel(
                    id, name ?: "",
                    order ?: 0, premium ?: false, status ?: "",
                    runId ?: "", runAttempt.id
                )
                sectionDao.insertNew(newSection)
            }
            getSectionContent(courseSection.id, runAttempt.id, courseSection.name)
        }
    }

    private suspend fun getSectionContent(sectionId: String, runAttemptId: String, name: String?) {
        when (val response = safeApiCall { Clients.onlineV2JsonApi.getSectionContents(sectionId) }) {
            is ResultWrapper.Success -> {
                if (response.value.isSuccessful)
                    response.value.body()?.let {
                        insertContents(it, runAttemptId, sectionId, name)
                    }
            }
        }
    }

    private suspend fun insertContents(contentList: List<LectureContent>, attemptId: String, sectionId: String, name: String?) {
        contentList.forEach { content ->
            var contentDocument =
                ContentDocument()
            var contentLecture =
                ContentLecture()
            var contentVideo =
                ContentVideo()
            var contentQna =
                ContentQnaModel()
            var contentCodeChallenge =
                ContentCodeChallenge()
            var contentCsv =
                ContentCsvModel()
            var bookmark =
                BookmarkModel()

            when (content.contentable) {
                "lecture" -> content.lecture?.let { contentLectureType ->
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
                "document" -> content.document?.let { contentDocumentType ->
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
                "video" -> content.video?.let { contentVideoType ->
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
                "qna" -> content.qna?.let { contentQna1 ->
                    contentQna =
                        ContentQnaModel(
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
                "code_challenge" -> content.codeChallenge?.let { codeChallenge ->
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
                "csv" -> content.csv?.let {
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
            content.bookmark?.let {
                bookmark = BookmarkModel(it.id ?: "",
                    it.runAttemptId ?: "",
                    it.contentId ?: "",
                    it.sectionId ?: "",
                    it.createdAt ?: "")
            }

            val newContent =
                ContentModel(
                    content.id,
                    status,
                    progressId,
                    content.title ?: "",
                    content.duration
                        ?: 0,
                    content.contentable ?: "",
                    content.sectionContent?.order
                        ?: 0,
                    attemptId,
                    name ?: "",
                    contentLecture,
                    contentDocument,
                    contentVideo,
                    contentQna,
                    contentCodeChallenge,
                    contentCsv
                )

            val oldModel: ContentModel? = contentsDao.getContent(content.id)
            if (oldModel != null && !oldModel.sameAndEqual(newContent)) {
                contentsDao.update(newContent)
            } else {
                contentsDao.insertNew(
                    newContent
                )
            }
            if (bookmark.bookmarkUid != "")
                bookmarkDao.insert(bookmark)

            sectionWithContentsDao.insert(
                SectionContentHolder.SectionWithContent(
                    sectionId,
                    content.id,
                    content.sectionContent?.order
                        ?: 0

                )
            )
        }
    }

    suspend fun fetchSections(attemptId: String) = safeApiCall { Clients.onlineV2JsonApi.enrolledCourseById(attemptId) }

    fun getRunById(attemptId: String) = courseWithInstructorDao.getRunById(attemptId)

    fun getRunStats(attemptId: String) = runPerformanceDao.getPerformance(attemptId)
    suspend fun getStats(id: String) = safeApiCall { Clients.api.getMyStats(id) }
    suspend fun saveStats(body: PerformanceResponse, id: String) {
        runPerformanceDao.insert(
            RunPerformance(
                id,
                body.performance?.percentile ?: 0,
                body.performance?.remarks ?: "Average",
                body.averageProgress,
                body.userProgress
            )
        )
    }

    fun getNextContent(attemptId: String) = sectionWithContentsDao.resumeCourse(attemptId)

    suspend fun resetProgress(attemptId: ResetRunAttempt) = safeApiCall { Clients.api.resetProgress(attemptId) }
}
