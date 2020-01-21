package com.codingblocks.cbonlineapp.library

import androidx.lifecycle.distinctUntilChanged
import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.database.LibraryDao
import com.codingblocks.cbonlineapp.database.NotesDao
import com.codingblocks.cbonlineapp.database.models.NotesModel
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Note
import com.codingblocks.onlineapi.safeApiCall

class LibraryRepository(
    private val notesDao: NotesDao,
    private val contentDao: ContentDao,
    private val libraryDao: LibraryDao

) {

    suspend fun fetchCourseNotes(attemptId: String) = safeApiCall { Clients.onlineV2JsonApi.getNotesByAttemptId(attemptId) }

    suspend fun insertNotes(notes: List<Note>) {
        notes.forEach {
            val model = NotesModel(
                it.id,
                it.duration,
                it.text,
                it.content?.id ?: "",
                it.runAttempt?.id ?: "",
                it.createdAt ?: "",
                it.deletedAt
            )
            notesDao.insert(model)
        }
    }

    fun getNotes(attemptId: String) = notesDao.getNotes(attemptId).distinctUntilChanged()

    fun getBookmarks(attemptId: String) = libraryDao.getBookmarks(attemptId).distinctUntilChanged()

    suspend fun fetchCourseBookmark(attemptId: String) = safeApiCall { Clients.onlineV2JsonApi.getBookmarksByAttemptId(id = attemptId) }
}
