package com.codingblocks.cbonlineapp.mycourse

import com.codingblocks.cbonlineapp.database.BookmarkDao
import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.database.CourseWithInstructorDao
import com.codingblocks.cbonlineapp.database.RunAttemptDao
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
import com.codingblocks.cbonlineapp.database.models.RunAttemptModel
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MyCourseRepository(
    private val sectionWithContentsDao: SectionWithContentsDao,
    private val contentsDao: ContentDao,
    private val sectionDao: SectionDao,
    private val courseWithInstructorDao: CourseWithInstructorDao,
    private val runPerformanceDao: RunPerformanceDao,
    private val bookmarkDao: BookmarkDao,
    private val attemptDao: RunAttemptDao
) {
    suspend fun getSectionWithContentNonLive(attemptId: String) = sectionWithContentsDao.getSectionWithContentNonLive(attemptId)

    fun getSectionWithContent(attemptId: String) = sectionWithContentsDao.getSectionWithContent(attemptId)

//    fun getSectionWithContentComputer(attemptId: String) = sectionWithContentsDao.getSectionWithContentComputed(SimpleSQLiteQuery("""
// SELECT s.*, swc.content_id as "content_id", c.contentDuration as "contentDuration", s."sectionOrder" as "sectionOrder", count (c.ccid)  as "completedContents" FROM SectionModel s LEFT OUTER join SectionWithContent swc on swc."section_id" = s.csid LEFT OUTER join ContentModel c on c.ccid = swc.content_id where s.attemptId = 44872 ORDER BY s."sectionOrder"         """))

    fun getRunById(attemptId: String) = courseWithInstructorDao.getRunById(attemptId)

    fun getRunStats(attemptId: String) = runPerformanceDao.getPerformance(attemptId)

    fun getNextContent(attemptId: String) = sectionWithContentsDao.resumeCourse(attemptId)

    suspend fun insertSections(runAttempt: RunAttempts, refresh: Boolean = false) {
        val runAttemptModel = RunAttemptModel(
            runAttempt.id,
            runAttempt.certificateApproved,
            runAttempt.end,
            runAttempt.premium,
            runAttempt.revoked,
            runAttempt.approvalRequested,
            runAttempt.doubtSupport ?: "",
            runAttempt.completedContents,
            runAttempt.lastAccessedAt ?: "",
            runAttempt.run?.id ?: "",
            runAttempt.certifcate?.url ?: "",
            runAttempt.runTier?:"PREMIUM"
        )
        attemptDao.update(runAttemptModel)

        runAttempt.run?.sections?.forEach { courseSection ->
            courseSection.run {
                val newSection = SectionModel(
                    id, name ?: "",
                    order ?: 0, premium ?: false, status ?: "",
                    runId ?: "", runAttempt.id
                )
                if (refresh)
                    sectionDao.insert(newSection)
                else
                    sectionDao.insertNew(newSection)
            }
            getSectionContent(courseSection.id, runAttempt.id, courseSection.name)
        }
        deleteOldSections(runAttempt.run?.sections?.map { it.id }!!, runAttempt.run?.id)
    }

    /**
     *Function to delete [SectionModel] which are no longer part of course content.
     */
    private suspend fun deleteOldSections(newList: List<String>, id: String?) {
        val oldList = withContext(Dispatchers.IO) { sectionDao.getCourseSection(id!!) }
        oldList.forEach {
            if (!newList.contains(it)) {
                sectionDao.deleteSection(it)
            }
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
                            content.sectionContent?.sectionId
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
                            content.sectionContent?.sectionId
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
                            content.sectionContent?.sectionId
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
                            content.sectionContent?.sectionId
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
                            content.sectionContent?.sectionId
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

    suspend fun resetProgress(attemptId: ResetRunAttempt) = safeApiCall { Clients.api.resetProgress(attemptId) }

    private suspend fun clearCart() = safeApiCall { Clients.api.clearCart() }

    suspend fun addToCart(id: String) = safeApiCall {
        clearCart()
        Clients.api.addToCart(id)
    }

    suspend fun fetchSections(attemptId: String) = safeApiCall { Clients.onlineV2JsonApi.enrolledCourseById(attemptId) }

    suspend fun getStats(id: String) = safeApiCall { Clients.api.getMyStats(id) }

    suspend fun requestApproval(attemptId: String) = safeApiCall { Clients.api.requestApproval(attemptId) }
}
