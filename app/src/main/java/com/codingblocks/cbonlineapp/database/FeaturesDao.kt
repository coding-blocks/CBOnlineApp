package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.codingblocks.cbonlineapp.database.models.CourseFeatures

@Dao
abstract class FeaturesDao : BaseDao<CourseFeatures> {

    @Query("SElECT * FROM CourseFeatures where crCourseId = :id ")
    abstract fun getfeatures(id: String): LiveData<List<CourseFeatures>>
}
