package com.codingblocks.cbonlineapp.mycourse.player.notes

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.codingblocks.cbonlineapp.database.NotesDao
import com.codingblocks.cbonlineapp.util.extensions.deserializeNoteFromJson
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Response

class NotesWorker(context: Context, private val workerParameters: WorkerParameters) : CoroutineWorker(context, workerParameters), KoinComponent {

    override suspend fun doWork(): Result {
        val notesDao: NotesDao by inject()
        val noteId = workerParameters.inputData.getString("NOTE_ID")
        val noteJson = workerParameters.inputData.getString("NOTE")
        val response: Response<Note> =
            if (noteId.isNullOrEmpty()) {
                val note = noteJson?.deserializeNoteFromJson()
                withContext(Dispatchers.IO) { Clients.onlineV2JsonApi.updateNoteById(note?.id ?: "", note!!) }
            } else {
                withContext(Dispatchers.IO) { Clients.onlineV2JsonApi.deleteNoteById(noteId ?: "") }
            }

        if (response.isSuccessful) {
            if (noteId.isNullOrEmpty()) {
                notesDao.updateBody(response.body()?.id ?: "", response.body()?.text ?: "")
            } else {
                noteId.let { notesDao.deleteNoteByID(it) }
            }
            return Result.success()
        } else {
            if (response.code() in (500..599)) {
                // try again if there is a server error
                return Result.retry()
            }
            return Result.failure()
        }
    }
}
