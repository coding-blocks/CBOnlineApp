package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class NotesDao : BaseDao<NotesModel> {
    @Query("SElECT * FROM NotesModel where runAttemptId = :ruid")
    abstract fun getNotes(ruid: String): LiveData<List<NotesModel>>

    @Query("SElECT * FROM NotesModel")
    abstract fun getAllNotes(): LiveData<List<NotesModel>>

    @Query("UPDATE NotesModel SET text = :text where nttUid = :uid")
    abstract fun updateBody(uid: String, text: String)

    @Query("SElECT * FROM NotesModel where nttUid = :uid")
    abstract fun getNoteById(uid: String): LiveData<NotesModel>

    @Query("DELETE FROM NotesModel where nttUid = :uid")
    abstract fun deleteNoteByID(uid: String)
}
