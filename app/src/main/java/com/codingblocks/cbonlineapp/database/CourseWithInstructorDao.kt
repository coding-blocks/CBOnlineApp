package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface CourseWithInstructorDao {


    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("""
        SELECT * FROM instructor INNER JOIN coursewithinstructor ON
        instructor.uid = coursewithinstructor.instructor_id WHERE
        coursewithinstructor.course_id = :courseID
        """)
    fun getInstructorWithCourseId(courseID: String): LiveData<List<Instructor>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(join: CourseWithInstructor)

}