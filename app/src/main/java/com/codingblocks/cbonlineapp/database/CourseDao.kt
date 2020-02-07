package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.codingblocks.cbonlineapp.database.models.CourseModel

@Dao
interface CourseDao : BaseDao<CourseModel> {

    @Query("SElECT * FROM CourseModel ")
    fun getCourses(): LiveData<List<CourseModel>>

    @Query("DELETE FROM CourseModel")
    suspend fun nukeTable()

    @Query("SElECT cid FROM CourseModel WHERE cid = :id")
    fun getCourseById(id: String): String
}
