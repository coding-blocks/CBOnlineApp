package com.codingblocks.cbonlineapp.mycourse

import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.database.CourseRunDao
import com.codingblocks.cbonlineapp.database.CourseWithInstructorDao
import com.codingblocks.cbonlineapp.database.SectionDao
import com.codingblocks.cbonlineapp.database.SectionWithContentsDao
import com.codingblocks.cbonlineapp.database.models.SectionModel
import com.codingblocks.onlineapi.models.CourseSection

class MyCourseRepository(
    private val runDao: CourseRunDao,
    private val sectionWithContentsDao: SectionWithContentsDao,
    private val contentsDao: ContentDao,
    private val sectionDao: SectionDao,
    private val instructorDao: CourseWithInstructorDao) {

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
}
