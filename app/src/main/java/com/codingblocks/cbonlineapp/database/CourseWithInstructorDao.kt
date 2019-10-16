package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.codingblocks.cbonlineapp.database.models.CourseInstructorPair
import com.codingblocks.cbonlineapp.database.models.CourseRunPair
import com.codingblocks.cbonlineapp.database.models.CourseWithInstructor
import com.codingblocks.cbonlineapp.database.models.InstructorModel

@Dao
interface CourseWithInstructorDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(join: CourseWithInstructor)

    @Query("""
        SELECT i.* FROM InstructorModel i
        INNER JOIN coursewithinstructor ON
        i.uid = coursewithinstructor.instructor_id
        WHERE coursewithinstructor.course_id = :courseID
        """)
    fun getInstructorWithCourseId(courseID: String): LiveData<List<InstructorModel>>

    @Transaction
    @Query("""
       SELECT c.*,r.* FROM RunModel r
	   INNER JOIN CourseModel c ON c.cid = r.crCourseId
       WHERE r.crAttemptId IS NULL
    """)
    fun getCourses(): DataSource.Factory<Int, CourseInstructorPair>


    @Transaction
    @Query("""
        SELECT c.*,r.* FROM RunModel r
	    INNER JOIN CourseModel c ON c.cid = r.crCourseId
        WHERE r.crAttemptId IS NULL AND recommended = 1
            """)
    fun getRecommendedCourses(): DataSource.Factory<Int, CourseInstructorPair>

    @Transaction
    @Query("""
    SELECT c.*,r.* FROM RunModel r
	   INNER JOIN CourseModel c ON c.cid = r.crCourseId
       WHERE r.crAttemptId IS NOT NULL ORDER BY hits DESC,crEnrollmentEnd DESC
    """)
    fun getMyRuns(): DataSource.Factory<Int, CourseInstructorPair>

    @Query("""
        SELECT * FROM RunModel r
	   INNER JOIN CourseModel c ON c.cid = r.crCourseId
       WHERE r.crAttemptId IS NOT NULL ORDER BY hits DESC LIMIT 2
    """)
    fun getTopRun(): LiveData<List<CourseRunPair>>
}
