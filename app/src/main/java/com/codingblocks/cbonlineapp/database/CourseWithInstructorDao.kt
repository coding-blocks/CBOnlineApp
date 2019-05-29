package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomWarnings
import com.codingblocks.cbonlineapp.database.models.CourseWithInstructor
import com.codingblocks.cbonlineapp.database.models.Instructor

@Dao
interface CourseWithInstructorDao {

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("""
        SELECT * FROM instructor
        INNER JOIN coursewithinstructor ON
        instructor.id = coursewithinstructor.instructor_id
        WHERE coursewithinstructor.course_id = :courseID
        """)
    fun getInstructorWithCourseId(courseID: String): LiveData<List<Instructor>>

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("""
        SELECT * FROM instructor
        INNER JOIN coursewithinstructor ON
        instructor.id = coursewithinstructor.instructor_id
        WHERE coursewithinstructor.course_id = :courseID
        """)
    fun getInstructorWithCourseIdNonLive(courseID: String): List<Instructor>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(join: CourseWithInstructor)
}
