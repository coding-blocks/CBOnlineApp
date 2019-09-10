package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.codingblocks.cbonlineapp.database.models.CourseInstructorHolder
import com.codingblocks.cbonlineapp.database.models.InstructorModel


@Dao
interface CourseWithInstructorDao {

    @Query("""
        SELECT * FROM InstructorModel i
        INNER JOIN coursewithinstructor ON
        i.uid = coursewithinstructor.instructor_id
        WHERE coursewithinstructor.course_id = :courseID
        """)
    fun getInstructorWithCourseId(courseID: String): LiveData<List<InstructorModel>>

    @Query("""
        SELECT * FROM InstructorModel i
        INNER JOIN coursewithinstructor ON
        i.uid = coursewithinstructor.instructor_id
        WHERE coursewithinstructor.course_id = :courseID
        """)
    fun getInstructorWithCourseIdNonLive(courseID: String): List<InstructorModel>


    @Query("""
    SELECT * FROM RunModel r,InstructorModel i
	   INNER JOIN CourseModel c ON (c.cid = r.crCourseId AND c.cid = ci.course_id)
	   INNER JOIN coursewithinstructor ci ON i.uid = ci.instructor_id
       WHERE r.crAttemptId IS NULL
    """)
    fun getAllCourses(): DataSource.Factory<Int, CourseInstructorHolder.CourseInstructorPair>

    @Query("""
    SELECT * FROM RunModel r,InstructorModel i
	   INNER JOIN CourseModel c ON (c.cid = r.crCourseId AND c.cid = ci.course_id)
	   INNER JOIN coursewithinstructor ci ON i.uid = ci.instructor_id
       WHERE r.crAttemptId IS NOT NULL ORDER BY hits DESC
    """)
    fun getMyCourses(): DataSource.Factory<Int, CourseInstructorHolder.CourseInstructorPair>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(join: CourseInstructorHolder.CourseWithInstructor)
}
