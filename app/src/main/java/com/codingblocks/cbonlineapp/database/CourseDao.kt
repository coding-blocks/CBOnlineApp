package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.codingblocks.cbonlineapp.database.models.CourseModel

@Dao
abstract class CourseDao : BaseDao<CourseModel> {

    @Query("SElECT * FROM CourseModel ")
    abstract fun getCourses(): LiveData<List<CourseModel>>

    @Query("DELETE FROM CourseModel")
    abstract suspend fun nukeTable()

    @Query("SElECT cid FROM CourseModel WHERE cid = :id")
    abstract fun getCourseById(id: String): String
}
