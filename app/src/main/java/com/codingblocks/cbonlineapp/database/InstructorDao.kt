package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.codingblocks.cbonlineapp.database.models.InstructorModel

@Dao
abstract class InstructorDao : BaseDao<InstructorModel> {

    @Query("SElECT * FROM InstructorModel ")
    abstract fun getInstructors(): LiveData<List<InstructorModel>>

    @Query("SElECT uid FROM InstructorModel WHERE uid = :id")
    abstract suspend fun getInstructorById(id: String): String
}
