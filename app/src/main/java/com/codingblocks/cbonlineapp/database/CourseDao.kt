package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class CourseDao : BaseDao<Course> {

    @Query("SElECT * FROM Course ")
    abstract fun getCourses(): LiveData<List<Course>>

}