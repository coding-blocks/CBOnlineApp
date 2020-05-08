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

    @Query("SElECT csid FROM SectionModel where run_id = :runId ORDER BY `sectionOrder`")
    suspend fun getCourseSection(runId: String): List<String>

    @Query("SElECT name FROM SectionModel where  csid = :id")
    suspend fun getSectionTitle(id: String): String

    @Query("DELETE FROM SectionModel where  csid = :id")
    fun deleteSection(id: String)
}
