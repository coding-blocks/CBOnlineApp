package com.codingblocks.cbonlineapp.mycourse.player

import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.database.CourseDao
import com.codingblocks.cbonlineapp.database.CourseRunDao
import com.codingblocks.cbonlineapp.database.DoubtsDao
import com.codingblocks.cbonlineapp.database.NotesDao
import com.codingblocks.cbonlineapp.database.SectionDao
import com.codingblocks.cbonlineapp.database.models.NotesModel
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Note
import com.codingblocks.onlineapi.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class VideoPlayerRepository(
    private val doubtsDao: DoubtsDao,
    private val notesDao: NotesDao,
    private val courseDao: CourseDao,
    private val runDao: CourseRunDao,
    private val contentDao: ContentDao,
    private val sectionDao: SectionDao
) {
    suspend fun fetchCourseNotes(attemptId: String) = safeApiCall { Clients.onlineV2JsonApi.getNotesByAttemptId(attemptId) }

    suspend fun deleteNote(noteId: String) = safeApiCall { Clients.onlineV2JsonApi.deleteNoteById(noteId) }


    suspend fun insertNotes(notes: List<Note>) {
        notes.forEach {
            val contentTitle = GlobalScope.async { contentDao.getContentTitle(it.content?.id ?: "") }
            val model = NotesModel(
                it.id,
                it.duration,
                it.text,
                it.content?.id ?: "",
                it.runAttempt?.id ?: "",
                it.createdAt ?: "",
                it.deletedAt,
                contentTitle.await()
            )
            notesDao.insert(model)
        }
    }

    fun updateNoteInDb(newNote: Note) {
        notesDao.updateBody(newNote.id, newNote.text)
    }

    fun getNotes(attemptId: String) = notesDao.getNotes(attemptId)

    fun deleteNoteFromDb(noteId: String) = notesDao.deleteNoteByID(noteId)

    suspend fun getSectionTitle(sectionId: String, contentId: String): Pair<String, String> {
        val a = withContext(Dispatchers.IO) { sectionDao.getSectionTitle(sectionId) }
        val b = withContext(Dispatchers.IO) { contentDao.getContentTitle(contentId) }
        return Pair(a, b)
    }

    suspend fun updateNote(note: Note) = safeApiCall { Clients.onlineV2JsonApi.updateNoteById(note.id, note) }


}
