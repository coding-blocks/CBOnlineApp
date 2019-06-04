package com.codingblocks.cbonlineapp.viewmodels

import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.database.CourseWithInstructorDao

class AnnouncementsViewModel(
    private val instructorDao: CourseWithInstructorDao
) : ViewModel() {
    fun getInstructorWithCourseId(courseID: String) = instructorDao.getInstructorWithCourseId(courseID)
}
