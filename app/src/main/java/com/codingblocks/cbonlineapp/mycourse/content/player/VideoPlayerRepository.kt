package com.codingblocks.cbonlineapp.mycourse.content.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.distinctUntilChanged
import com.codingblocks.cbonlineapp.database.BookmarkDao
import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.database.DoubtsDao
import com.codingblocks.cbonlineapp.database.NotesDao
import com.codingblocks.cbonlineapp.database.PlayerDao
import com.codingblocks.cbonlineapp.database.SectionWithContentsDao
import com.codingblocks.cbonlineapp.database.models.BookmarkModel
import com.codingblocks.cbonlineapp.database.models.DoubtsModel
import com.codingblocks.cbonlineapp.database.models.NotesModel
import com.codingblocks.cbonlineapp.database.models.PlayerState
import com.codingblocks.cbonlineapp.util.LIVE
import com.codingblocks.cbonlineapp.util.RESOLVED
import com.codingblocks.onlineapi.CBOnlineLib
import com.codingblocks.onlineapi.models.Bookmark
import com.codingblocks.onlineapi.models.Doubts
import com.codingblocks.onlineapi.models.Note
import com.codingblocks.onlineapi.safeApiCall

class VideoPlayerRepository(
    private val notesDao: NotesDao,
    private val contentDao: ContentDao,
    private val bookmarkDao: BookmarkDao,
    private val sectionDao: SectionWithContentsDao,
    private val playerDao: PlayerDao,
    private val doubtsDao: DoubtsDao
) {
    suspend fun fetchCourseContentNotes(attemptId: String, contentId: String) =
        safeApiCall { CBOnlineLib.onlineV2JsonApi.getNotesForContent(attemptId, contentId) }

    suspend fun fetchCourseContentDoubts(attemptId: String, contentId: String) =
        safeApiCall { CBOnlineLib.onlineV2JsonApi.getDoubtsForContent(attemptId, contentId) }

    fun getDoubtsByCourseRun(type: String?, pair: Pair<String?, String?>): LiveData<List<DoubtsModel>> {
        return when (type) {
            LIVE -> doubtsDao.getLiveDoubtsForContent(pair.first!!, pair.second!!)
            RESOLVED -> doubtsDao.getResolveDoubtsForContent(pair.first!!, pair.second!!)
            else -> doubtsDao.getDoubtsForContent(pair.first!!, pair.second!!).distinctUntilChanged()
        }
    }

    suspend fun deleteNote(noteId: String) = safeApiCall { CBOnlineLib.onlineV2JsonApi.deleteNoteById(noteId) }

    fun deleteNoteFromDb(noteId: String) = notesDao.deleteNoteByID(noteId)

    suspend fun addNote(note: Note) = safeApiCall { CBOnlineLib.onlineV2JsonApi.createNote(note) }

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

    fun getNotes(pair: Pair<String?, String?>) = notesDao.getNotesForContent(pair.second!!, pair.first!!)

    suspend fun updateNote(note: Note) = safeApiCall { CBOnlineLib.onlineV2JsonApi.updateNoteById(note.id, note) }

    suspend fun getOtp(videoId: String, attemptId: String, sectionId: String) =
        safeApiCall { CBOnlineLib.api.getOtp(videoId, sectionId, attemptId) }

    suspend fun markDoubt(bookmark: Bookmark) = safeApiCall { CBOnlineLib.onlineV2JsonApi.addBookmark(bookmark) }

    suspend fun updateBookmark(bookmark: Bookmark) {
        bookmarkDao.insert(
            BookmarkModel(
                bookmark.id ?: "",
                bookmark.runAttempt?.id ?: "",
                bookmark.content?.id ?: "",
                bookmark.section?.id ?: "",
                bookmark.createdAt ?: ""
            )
        )
    }

    fun getContents(attemptId: String, sectionId: String) =
        sectionDao.getNextContent(attemptId, sectionId).distinctUntilChanged()

    suspend fun removeBookmark(bookmarkUid: String) =
        safeApiCall { CBOnlineLib.onlineV2JsonApi.deleteBookmark(bookmarkUid) }

    fun deleteBookmark(id: String) = bookmarkDao.deleteBookmark(id)

    suspend fun addDoubt(doubt: Doubts) = safeApiCall { CBOnlineLib.onlineV2JsonApi.createDoubt(doubt) }

    fun getBookmark(contentId: String) = bookmarkDao.getBookmarkById(contentId)

    suspend fun updateDownload(status: Int, lectureId: String) =
        contentDao.updateContentWithVideoId(lectureId, status)

    suspend fun savePlayerState(attemptId: String, sectionId: String, contentId: String, time: Long) {
        playerDao.insert(PlayerState(attemptId, sectionId, contentId, time))
    }

    fun deletePlayerState(attemptId: String) {
        playerDao.deleteById(attemptId)
    }
}
