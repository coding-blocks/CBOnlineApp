package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.codingblocks.cbonlineapp.database.models.CourseFeatureModel
import com.codingblocks.onlineapi.models.CourseFeatures

@Dao
abstract class FeaturesDao : BaseDao<CourseFeatureModel> {

    @Query("SElECT * FROM CourseFeatureModel where crCourseId = :id ")
    abstract fun getfeatures(id: String): LiveData<List<CourseFeatures>>
}
