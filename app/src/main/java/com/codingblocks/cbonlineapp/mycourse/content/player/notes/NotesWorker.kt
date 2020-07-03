package com.codingblocks.cbonlineapp.mycourse.content.player.notes

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.codingblocks.cbonlineapp.database.NotesDao
import com.codingblocks.cbonlineapp.util.extensions.deserializeNoteFromJson
import com.codingblocks.onlineapi.CBOnlineLib
import com.codingblocks.onlineapi.models.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject

class NotesWorker(context: Context, private val workerParameters: WorkerParameters) : CoroutineWorker(context, workerParameters), KoinComponent {

    override suspend fun doWork(): Result {
        val notesDao: NotesDao by inject()
        val noteId = workerParameters.inputData.getString("NOTE_ID")
        val noteJson = workerParameters.inputData.getString("NOTE")
        val doubtJson = workerParameters.inputData.getString("DOUBT")
        val response = when {
            noteId.isNullOrEmpty() -> {
                val note: Note = noteJson?.deserializeNoteFromJson()!!
                withContext(Dispatchers.IO) { CBOnlineLib.onlineV2JsonApi.updateNoteById(note.id, note) }
            }
            noteJson.isNullOrEmpty() -> {
                withContext(Dispatchers.IO) { CBOnlineLib.onlineV2JsonApi.deleteNoteById(noteId) }
            }
            else -> {
                withContext(Dispatchers.IO) { CBOnlineLib.onlineV2JsonApi.createDoubt(doubtJson?.deserializeNoteFromJson()!!) }
            }
        }

        if (response.isSuccessful) {
            when {
                noteId.isNullOrEmpty() -> {
                    notesDao.updateBody(response.body()?.id ?: "", (response.body() as Note).text)
                }
                noteJson.isNullOrEmpty() -> {
                    noteId.let { notesDao.deleteNoteByID(it) }
                }
                else -> {
                }
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
