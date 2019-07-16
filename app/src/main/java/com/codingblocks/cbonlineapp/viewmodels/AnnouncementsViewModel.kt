package com.codingblocks.cbonlineapp.viewmodels

import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.database.CourseRunDao
import com.codingblocks.cbonlineapp.database.CourseWithInstructorDao

class AnnouncementsViewModel(
    private val instructorDao: CourseWithInstructorDao,
    private val runDao: CourseRunDao
) : ViewModel() {
    fun getInstructorWithCourseId(courseID: String) = instructorDao.getInstructorWithCourseId(courseID)
    fun getRunByAtemptId(attemptId: String) = runDao.getRunByAtemptId(attemptId)
}
