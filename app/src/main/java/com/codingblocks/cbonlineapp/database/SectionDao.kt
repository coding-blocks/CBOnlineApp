package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.codingblocks.cbonlineapp.database.models.SectionModel

@Dao
abstract class SectionDao : BaseDao<SectionModel> {

    @Query("SElECT * FROM SectionModel ")
    abstract fun getSections(): LiveData<List<SectionModel>>

    @Query("SElECT * FROM SectionModel where csid = :id")
    abstract fun getSectionWithId(id: String): SectionModel

    @Query("SElECT * FROM SectionModel where attemptId = :courseId ORDER BY `sectionOrder`")
    abstract fun getCourseSection(courseId: String): LiveData<List<SectionModel>>
}
