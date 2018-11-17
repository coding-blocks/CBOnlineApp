package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MyCourseDao {

    @Insert
    fun addData(course: MyCourseRuns): Long //long to return id of course added

    @Query("SElECT * FROM courseData where id = :courseId")
    fun getCourse(courseId: String): LiveData<MyCourseRuns>

    @Delete
    fun delete(course: MyCourseRuns)

    @Update
    fun update(course: MyCourseRuns)


    @Transaction
    @Query("SELECT * FROM MyCourseRun")
    fun getAllSections(): LiveData<AllCourseSection>

    class AllCourseSection {
        @Embedded
        lateinit var course: MyCourseRuns

        @Relation(parentColumn = "id", entityColumn = "run_id")
        lateinit var sections: List<CourseSection>

    }
}