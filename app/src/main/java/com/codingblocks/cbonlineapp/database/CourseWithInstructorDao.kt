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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(join: CourseInstructorHolder.CourseWithInstructor)

    @Query("""
        SELECT i.* FROM InstructorModel i
        INNER JOIN coursewithinstructor ON
        i.uid = coursewithinstructor.instructor_id
        WHERE coursewithinstructor.course_id = :courseID
        """)
    fun getInstructorWithCourseId(courseID: String): LiveData<List<InstructorModel>>

    @Query("""
    SELECT c.*,r.*,i.* FROM RunModel r
	   INNER JOIN CourseModel c ON c.cid = r.crCourseId
	   INNER JOIN CourseWithInstructor ci ON ci.course_id = c.cid
       INNER JOIN InstructorModel i ON i.uid = ci.instructor_id
       WHERE r.crAttemptId IS NULL
    """)
    fun getAllCourses(): DataSource.Factory<Int, CourseInstructorHolder.CourseInstructorPair>

    @Query("""
    SELECT c.*,r.*,i.* FROM RunModel r
	   INNER JOIN CourseModel c ON c.cid = r.crCourseId
	   INNER JOIN CourseWithInstructor ci ON ci.course_id = c.cid
       INNER JOIN InstructorModel i ON i.uid = ci.instructor_id
       WHERE r.crAttemptId IS NOT NULL ORDER BY hits DESC
    """)
    fun getMyRuns(): DataSource.Factory<Int, CourseInstructorHolder.CourseInstructorPair>

    @Query("""
        SELECT c.*,r.* FROM RunModel r
	   INNER JOIN CourseModel c ON c.cid = r.crCourseId
       WHERE r.crAttemptId IS NOT NULL ORDER BY hits DESC LIMIT 2
    """)
    fun getTopRun(): LiveData<List<CourseInstructorHolder.CourseRunPair>>
}
