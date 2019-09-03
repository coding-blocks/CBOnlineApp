package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.codingblocks.cbonlineapp.database.models.CourseSection

@Dao
abstract class SectionDao : BaseDao<CourseSection> {

    @Query("SElECT * FROM CourseSection ")
    abstract fun getSections(): LiveData<List<CourseSection>>

    @Query("SElECT * FROM CourseSection where csid = :id")
    abstract fun getSectionWithId(id: String): CourseSection

    @Query("SElECT * FROM CourseSection where attemptId = :courseId ORDER BY `sectionOrder`")
    abstract fun getCourseSection(courseId: String): LiveData<List<CourseSection>>
}
