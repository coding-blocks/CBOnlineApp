package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.codingblocks.onlineapi.models.MyCourse

@Dao
interface MyCourseDao {

    @Insert
    fun addData(course: MyCourse): Long //long to return id of course added

    @Query("SElECT * FROM courseData where id = :courseId")
    fun getCourse(courseId: String): LiveData<MyCourse>

    @Delete
    fun delete(course: MyCourse)

    @Update
    fun update(course: MyCourse)
}