package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class NotesDao : BaseDao<NotesModel> {

    @Query("SElECT * FROM NotesModel where runAttemptId = :ruid")
    abstract fun getNotess(ruid: String): LiveData<List<NotesModel>>

    @Query("UPDATE NotesModel SET text = :text where dbtUid = :uid")
    abstract fun updateBody(uid: String,text:String)

    @Query("SElECT * FROM DoubtsModel where dbtUid = :uid")
    abstract fun getNoteById(uid: String): LiveData<NotesModel>

}