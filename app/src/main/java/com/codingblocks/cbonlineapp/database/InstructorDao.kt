package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.codingblocks.cbonlineapp.database.models.Instructor

@Dao
abstract class InstructorDao : BaseDao<Instructor> {

    @Query("SElECT * FROM Instructor ")
    abstract fun getInstructors(): LiveData<List<Instructor>>
}
