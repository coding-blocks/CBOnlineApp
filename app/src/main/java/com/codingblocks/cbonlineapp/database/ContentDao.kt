package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class ContentDao : BaseDao<CourseContent> {

    @Query("SElECT * FROM CourseContent ")
    abstract fun getContent(): LiveData<List<CourseContent>>

}