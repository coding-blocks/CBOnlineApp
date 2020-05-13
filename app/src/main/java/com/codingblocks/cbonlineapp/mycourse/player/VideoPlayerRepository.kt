package com.codingblocks.cbonlineapp.mycourse.player

import androidx.lifecycle.distinctUntilChanged
import com.codingblocks.cbonlineapp.database.BookmarkDao
import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.database.NotesDao
import com.codingblocks.cbonlineapp.database.PlayerDao
import com.codingblocks.cbonlineapp.database.SectionWithContentsDao
import com.codingblocks.cbonlineapp.database.models.BookmarkModel
import com.codingblocks.cbonlineapp.database.models.NotesModel
import com.codingblocks.cbonlineapp.database.models.PlayerState
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Bookmark
import com.codingblocks.onlineapi.models.Doubts
import com.codingblocks.onlineapi.models.Note
import com.codingblocks.onlineapi.safeApiCall

class VideoPlayerRepository(
    private val notesDao: NotesDao,
    private val contentDao: ContentDao,
    private val bookmarkDao: BookmarkDao,
    private val sectionDao: SectionWithContentsDao,
    private val playerDao: PlayerDao
) {
    suspend fun fetchCourseNotes(attemptId: String) = safeApiCall { Clients.onlineV2JsonApi.getNotesByAttemptId(attemptId) }

    suspend fun deleteNote(noteId: String) = safeApiCall { Clients.onlineV2JsonApi.deleteNoteById(noteId) }

    fun deleteNoteFromDb(noteId: String) = notesDao.deleteNoteByID(noteId)

    suspend fun addNote(note: Note) = safeApiCall { Clients.onlineV2JsonApi.createNote(note) }

    fun getContent(ccid: String) = contentDao.getContentLive(ccid).distinctUntilChanged()

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
//            val sum = notes + networkList
//            sum.groupBy { it.nttUid }
//                .filter { it.value.size == 1 }
//                .flatMap { it.value }
//                .forEach {
//                    notesDao.deleteNoteByID(it.nttUid)
//                }
            notesDao.insert(model)
        }
    }

    fun updateNoteInDb(newNote: Note) {
        notesDao.updateBody(newNote.id, newNote.text)
    }

    suspend fun addNewNote(newNote: Note) {
        val model = NotesModel(
            newNote.id,
            newNote.duration,
            newNote.text,
            newNote.content?.id ?: "",
            newNote.runAttempt?.id ?: "",
            newNote.createdAt ?: "",
            newNote.deletedAt
        )
        notesDao.insert(model)
    }

    fun getNotes(attemptId: String) = notesDao.getNotes(attemptId)

    suspend fun updateNote(note: Note) = safeApiCall { Clients.onlineV2JsonApi.updateNoteById(note.id, note) }

    suspend fun getOtp(videoId: String, attemptId: String, sectionId: String) =
        safeApiCall { Clients.api.getOtp(videoId, sectionId, attemptId) }

    suspend fun markDoubt(bookmark: Bookmark) = safeApiCall { Clients.onlineV2JsonApi.addBookmark(bookmark) }

    suspend fun updateBookmark(bookmark: Bookmark) {
        bookmarkDao.insert(BookmarkModel(bookmark.id ?: "",
            bookmark.runAttempt?.id ?: "",
            bookmark.content?.id ?: "",
            bookmark.section?.id ?: "",
            bookmark.createdAt ?: ""))
    }

    fun getContents(attemptId: String, sectionId: String) = sectionDao.getNextContent(attemptId, sectionId).distinctUntilChanged()

    suspend fun removeBookmark(bookmarkUid: String) = safeApiCall { Clients.onlineV2JsonApi.deleteBookmark(bookmarkUid) }

    fun deleteBookmark(id: String) = bookmarkDao.deleteBookmark(id)

    suspend fun addDoubt(doubt: Doubts) = safeApiCall { Clients.onlineV2JsonApi.createDoubt(doubt) }

    fun getBookmark(contentId: String) = bookmarkDao.getBookmarkById(contentId)

    suspend fun updateDownload(status: Int, lectureId: String) = contentDao.updateContentWithVideoId(lectureId, status)

    suspend fun savePlayerState(attemptId: String, sectionId: String, contentId: String, time: Long) {
        playerDao.insert(PlayerState(attemptId, sectionId, contentId, time))
    }

    fun deletePlayerState(attemptId: String) {
        playerDao.deleteById(attemptId)
    }
}
