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
import com.codingblocks.onlineapi.models.CourseSection
import com.codingblocks.onlineapi.models.LectureContent

class MyCourseRepository(
    private val runDao: CourseRunDao,
    private val sectionWithContentsDao: SectionWithContentsDao,
    private val contentsDao: ContentDao,
    private val sectionDao: SectionDao,
    private val instructorDao: CourseWithInstructorDao
) {

    fun getInstructorWithCourseId(courseId: String) = instructorDao.getInstructorWithCourseId(courseId)

    fun getSectionWithContent(attemptId: String) = sectionWithContentsDao.getSectionWithContent(attemptId)

    fun updateHit(attemptId: String) = runDao.updateHit(attemptId)

    fun resumeCourse(attemptId: String) = sectionWithContentsDao.resumeCourse(attemptId)

    fun run(runId: String) = runDao.getRun(runId)

    suspend fun insertSections(sectionList: ArrayList<CourseSection>, attemptId: String) {
        sectionList.forEach { courseSection ->
            courseSection.run {
                val newSection = SectionModel(
                    id, name,
                    order, premium, status,
                    runId, attemptId
                )
                val oldSection = sectionDao.getSectionWithId(id)
                if (oldSection == null)
                    sectionDao.insert(newSection)
                else if (oldSection == newSection) {
                    sectionDao.update(newSection)
                }
            }
        }
    }

    suspend fun insertContents(contentList: ArrayList<LectureContent>, attemptId: String, sectionId: String) {
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
            contentsDao.insert(
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
}
