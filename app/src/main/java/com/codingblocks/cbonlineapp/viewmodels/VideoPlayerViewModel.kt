package com.codingblocks.cbonlineapp.viewmodels

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.adapters.VideosNotesAdapter
import com.codingblocks.cbonlineapp.database.DoubtsDao
import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.database.CourseDao
import com.codingblocks.cbonlineapp.database.NotesDao
import com.codingblocks.cbonlineapp.database.CourseRunDao
import com.codingblocks.cbonlineapp.database.models.DoubtsModel
import com.codingblocks.cbonlineapp.database.models.NotesModel
import com.codingblocks.cbonlineapp.extensions.observeOnce
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.cbonlineapp.extensions.secToTime
import com.codingblocks.cbonlineapp.observables.NotesObservables
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.ContentsId
import com.codingblocks.onlineapi.models.DoubtsJsonApi
import com.codingblocks.onlineapi.models.Notes
import com.codingblocks.onlineapi.models.RunAttemptsId
import java.lang.Exception

class VideoPlayerViewModel(
    private val doubtsDao: DoubtsDao,
    private val notesDao: NotesDao,
    private val courseDao: CourseDao,
    private val runDao: CourseRunDao,
    private val contentDao: ContentDao
) : ViewModel() {
    var attemptId = ""

    var playWhenReady = false
    var currentOrientation: Int = 0
    var mOtp: String? = null
    var mPlaybackInfo: String? = null

    var getOtpProgress: MutableLiveData<Boolean> = MutableLiveData()
    var createDoubtProgress: MutableLiveData<Boolean> = MutableLiveData()
    var createNoteProgress: MutableLiveData<Boolean> = MutableLiveData()

    // Notes Adapter Declarations
    private var notesAdapter = VideosNotesAdapter(this)
    var notesObservables = NotesObservables()
    var deleteNote: MutableLiveData<Int> = MutableLiveData()
    var deletedNote: NotesModel = NotesModel()
    var updatedNote: MutableLiveData<Boolean> = MutableLiveData()

    var selectedNote: MutableLiveData<NotesModel> = MutableLiveData()

    fun getRunByAttemptId() = runDao.getRunByAtemptId(attemptId)

    fun getCourseById(id: String) = courseDao.getCourse(id)

    fun getContentWithId(attemptId: String, contentId: String) = contentDao.getContentWithId(attemptId, contentId)

    private fun deleteNoteByID(id: String) = notesDao.deleteNoteByID(id)

    private fun updateNote(notes: NotesModel) = notesDao.update(notes)

    fun updateDoubtStatus(uid: String, status: String) = doubtsDao.updateStatus(uid, status)

    fun getNotes() = notesDao.getNotes(attemptId)

    fun getDoubts(ruid: String) = doubtsDao.getDoubts(ruid)

    fun getOtp(videoId: String, sectionId: String) {
        Clients.api.getOtp(videoId, sectionId, attemptId)
            .enqueue(retrofitCallback { _, response ->
                response?.let {
                    mOtp = response.body()?.get("otp")?.asString
                    mPlaybackInfo = response.body()?.get("playbackInfo")?.asString
                    getOtpProgress.value = (it.isSuccessful)
                }
            })
    }

    fun createDoubt(doubt: DoubtsJsonApi) {
        Clients.onlineV2JsonApi.createDoubt(doubt)
            .enqueue(retrofitCallback { _, response ->
                try {
                    if ((response?.isSuccessful == true))
                        response.body()?.let {
                            doubtsDao.insert(
                                DoubtsModel(
                                    it.id, it.title, it.body, it.contents?.id
                                    ?: "", it.status, attemptId
                                )
                            )
                        }
                } catch (e: Exception) {
                    createDoubtProgress.value = false
                }
                createDoubtProgress.value = (response?.isSuccessful == true)
            })
    }

    fun createNote(note: Notes) {
        Clients.onlineV2JsonApi.createNote(note)
            .enqueue(retrofitCallback { _, response ->
                response?.let { responseNote ->
                    createNoteProgress.value = true
                    if (responseNote.isSuccessful)
                        responseNote.body().let {
                            try {
                                notesDao.insert(
                                    NotesModel(
                                        it?.id ?: "",
                                        it?.duration ?: 0.0,
                                        it?.text ?: "",
                                        it?.content?.id ?: "",
                                        attemptId,
                                        it?.createdAt ?: "",
                                        it?.deletedAt ?: ""
                                    )
                                )
                            } catch (e: Exception) {
                                createNoteProgress.value = false
                            }
                        }
                    else
                        createNoteProgress.value = false
                }
            })
    }

    fun fetchDoubts(param: String) {
        Clients.onlineV2JsonApi.getDoubtByAttemptId(param).enqueue(retrofitCallback { _, response ->
            response?.body().let { doubts ->
                if (response != null && response.isSuccessful) {
                    doubts?.forEach {
                        try {
                            doubtsDao.insert(
                                DoubtsModel(
                                    it.id, it.title, it.body, it.content?.id
                                    ?: "", it.status, it.runAttempt?.id ?: "",
                                    it.discourseTopicId
                                )
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        })
    }

    fun fetchNotes() {
        val networkList: ArrayList<NotesModel> = ArrayList()
        Clients.onlineV2JsonApi.getNotesByAttemptId(attemptId).enqueue(retrofitCallback { _, response ->
            response?.body().let { notesList ->
                if (response?.isSuccessful == true) {
                    notesList?.forEach {
                        try {
                            networkList.add(
                                NotesModel(
                                    it.id,
                                    it.duration ?: 0.0,
                                    it.text ?: "",
                                    it.content?.id
                                        ?: "",
                                    it.runAttempt?.id ?: "",
                                    it.createdAt ?: "",
                                    it.deletedAt
                                        ?: ""
                                )
                            )
                        } catch (e: Exception) {
                        }
                    }
                    if (networkList.size == notesList?.size) {
                        notesDao.insertAll(networkList)
                        notesDao.getNotes(attemptId).observeOnce { list ->
                            // remove items which are deleted
                            val sum = list + networkList
                            sum.groupBy { it.nttUid }
                                .filter { it.value.size == 1 }
                                .flatMap { it.value }
                                .forEach {
                                    notesDao.deleteNoteByID(it.nttUid)
                                }
                        }
                    }
                }
            }
        })
    }

    // Notes Adapter

    fun getAdapter() = notesAdapter

    fun onNoteClick(position: Int) {
        selectedNote.value = notesAdapter.getNoteAt(position)
    }

    fun getNoteAt(position: Int) = notesAdapter.getNoteAt(position)

    fun getNoteText(position: Int) = notesObservables.bodyTvText[position]

    fun fetchItemsAt(position: Int) {
        ObservableField<String>().let {
            it.set(getNoteAt(position).text)
            notesObservables.bodyTvText.add(position, it)
        }
        ObservableField<Boolean>().let {
            it.set(false)
            notesObservables.bodyTvEnabled.add(position, it)
        }
        ObservableField<String>().let {
            it.set("Edit")
            notesObservables.editTvText.add(position, it)
        }
        ObservableField<String>().let {
            it.set("Delete")
            notesObservables.deleteTvText.add(position, it)
        }
    }

    fun getNoteContentTitle(position: Int) = getContentWithId(notesAdapter.getNoteAt(position).runAttemptId, notesAdapter.getNoteAt(position).contentId).title

    fun getNoteTime(position: Int) = secToTime(notesAdapter.getNoteAt(position).duration)

    fun setOnEditClick(position: Int) {
        if (notesObservables.editTvText[position].get() == "Edit") {
            notesObservables.editTvText[position].set("Save")
            notesObservables.deleteTvText[position].set("Cancel")
            notesObservables.bodyTvEnabled[position].set(true)
        } else
            updateEditedNote(getNoteAt(position), position)
    }

    private fun updateEditedNote(notesModel: NotesModel, position: Int) {
        val note = Notes()
        note.text = notesObservables.editTvText[position].get()
        note.duration = notesModel.duration
        note.runAttempt = RunAttemptsId(notesModel.runAttemptId)
        note.content = ContentsId(notesModel.contentId)
        notesModel.text = notesObservables.bodyTvText[position].get().toString()
        Clients.onlineV2JsonApi.updateNoteById(notesModel.nttUid, note)
            .enqueue(retrofitCallback { _, response ->
                response?.body().let {
                    updatedNote.value = (response?.isSuccessful == true)
                    if (response?.isSuccessful == true)
                        try {
                            notesObservables.editTvText[position].set("Edit")
                            notesObservables.deleteTvText[position].set("Delete")
                            notesObservables.bodyTvEnabled[position].set(false)
                            updateNote(notesModel)
                        } catch (e: Exception) {
                        }
                }
            })
    }

    fun setOnDeleteClick(position: Int) {
        if (notesObservables.deleteTvText[position].get() == "Delete") {
            deletedNote = notesAdapter.getNoteAt(position)
            notesAdapter.notesData.removeAt(position)
            notesAdapter.notifyItemRemoved(position)
            deleteNote.value = position
        } else {
            notesObservables.editTvText[position].set("Edit")
            notesObservables.deleteTvText[position].set("Delete")
            notesObservables.bodyTvEnabled[position].set(false)
            notesObservables.bodyTvText[position].set(getNoteAt(position).text)
        }
    }

    fun deleteNoteById() {
        Clients.onlineV2JsonApi.deleteNoteById(deletedNote.nttUid)
            .enqueue(retrofitCallback { _, response ->
                response.let {
                    if (it?.isSuccessful == true) {
                        deleteNoteByID(deletedNote.nttUid)
                    }
                }
            })
    }
}
