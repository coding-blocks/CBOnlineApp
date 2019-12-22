package com.codingblocks.cbonlineapp.mycourse

import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.database.CourseRunDao
import com.codingblocks.cbonlineapp.database.CourseWithInstructorDao
import com.codingblocks.cbonlineapp.database.SectionDao
import com.codingblocks.cbonlineapp.database.SectionWithContentsDao
import com.codingblocks.cbonlineapp.database.models.ContentCodeChallenge
import com.codingblocks.cbonlineapp.database.models.ContentCsvModel
import com.codingblocks.cbonlineapp.database.models.ContentDocument
import com.codingblocks.cbonlineapp.database.models.ContentLecture
import com.codingblocks.cbonlineapp.database.models.ContentModel
import com.codingblocks.cbonlineapp.database.models.ContentQna
import com.codingblocks.cbonlineapp.database.models.ContentVideo
import com.codingblocks.cbonlineapp.database.models.SectionContentHolder
import com.codingblocks.cbonlineapp.database.models.SectionModel
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.models.LectureContent
import com.codingblocks.onlineapi.models.RunAttempts
import com.codingblocks.onlineapi.safeApiCall

class MyCourseRepository(
    private val runDao: CourseRunDao,
    private val sectionWithContentsDao: SectionWithContentsDao,
    private val contentsDao: ContentDao,
    private val sectionDao: SectionDao,
    private val instructorDao: CourseWithInstructorDao
) {

    fun getInstructorWithCourseId(courseId: String) = instructorDao.getInstructorWithCourseId(courseId)

    fun getSectionWithContent(attemptId: String) = sectionWithContentsDao.getSectionWithContent(attemptId)

    suspend fun updateHit(attemptId: String) = runDao.updateHit(attemptId)

    fun resumeCourse(attemptId: String) = sectionWithContentsDao.resumeCourse(attemptId)

    fun run(runId: String) = runDao.getRun(runId)

    suspend fun
        insertSections(runAttempt: RunAttempts) {
        runAttempt.run?.sections?.forEach { courseSection ->
            courseSection.run {
                val newSection = SectionModel(
                    id, name ?: "",
                    order ?: 0, premium ?: false, status ?: "",
                    runId ?: "", runAttempt.id
                )
                sectionDao.insertNew(newSection)
            }
            courseSection.courseContentLinks?.related?.href?.substring(7)?.let {
                getSectionContent(courseSection.id, runAttempt.id, it)
            }
        }

    }

    private suspend fun getSectionContent(sectionId: String, runAttemptId: String, sectionLink: String) {
        when (val response = safeApiCall { Clients.onlineV2JsonApi.getSectionContents(sectionLink) }) {
            is ResultWrapper.Success -> {
                if (response.value.isSuccessful)
                    response.value.body()?.let {
                        insertContents(it, runAttemptId, sectionId)
                    }
            }

        }
    }

    private suspend fun insertContents(contentList: List<LectureContent>, attemptId: String, sectionId: String) {
        contentList.forEach { content ->
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
            contentsDao.insertNew(
                newContent
            )
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
}
