package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.codingblocks.cbonlineapp.database.models.SectionModel

@Dao
interface SectionDao : BaseDao<SectionModel> {

    @Query("SElECT * FROM SectionModel ")
    fun getSections(): LiveData<List<SectionModel>>

    @Query("SElECT * FROM SectionModel where csid = :id")
    abstract fun getSectionWithId(id: String): SectionModel

    @Query("SElECT * FROM SectionModel where attemptId = :courseId ORDER BY `sectionOrder`")
    fun getCourseSection(courseId: String): LiveData<List<SectionModel>>

    @Query("SElECT name FROM SectionModel where  csid = :id")
    suspend fun getSectionTitle(id: String): String
}
