package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class InstructorDao : BaseDao<Instructor> {

    @Query("SElECT * FROM Instructor ")
    abstract fun getInstructors(): LiveData<List<Instructor>>

    @Query("SElECT * FROM Instructor where attempt_id = :courseId ")
    abstract fun getInstructors(courseId: String): LiveData<List<Instructor>>

}