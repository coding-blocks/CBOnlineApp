package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class SectionDao : BaseDao<CourseSection> {

    @Query("SElECT * FROM CourseSection ")
    abstract fun getSections(): LiveData<List<CourseSection>>

}