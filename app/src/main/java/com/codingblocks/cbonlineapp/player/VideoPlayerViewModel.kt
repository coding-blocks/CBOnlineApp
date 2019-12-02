package com.codingblocks.cbonlineapp.player

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.database.CourseDao
import com.codingblocks.cbonlineapp.database.CourseRunDao
import com.codingblocks.cbonlineapp.database.DoubtsDao
import com.codingblocks.cbonlineapp.database.NotesDao
import com.codingblocks.cbonlineapp.database.models.DoubtsModel
import com.codingblocks.cbonlineapp.database.models.NotesModel
import com.codingblocks.cbonlineapp.util.extensions.observeOnce
import com.codingblocks.cbonlineapp.util.extensions.retrofitCallback
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Doubts
import com.codingblocks.onlineapi.models.Notes
import com.crashlytics.android.Crashlytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VideoPlayerViewModel(
    private val doubtsDao: DoubtsDao,
    private val notesDao: NotesDao,
    private val courseDao: CourseDao,
    private val runDao: CourseRunDao,
    private val contentDao: ContentDao
) : ViewModel() {
    var playWhenReady = false
    var currentOrientation: Int = 0
    var mOtp: String? = null
    var mPlaybackInfo: String? = null

    var getOtpProgress: MutableLiveData<Boolean> = MutableLiveData()
    var createDoubtProgress: MutableLiveData<Boolean> = MutableLiveData()
    var createNoteProgress: MutableLiveData<Boolean> = MutableLiveData()

    fun getRunByAtemptId(id: String) = runDao.getRunByAtemptId(id)

    fun getCourseById(id: String) = courseDao.getCourses().value!![0]

    fun getContentWithId(attemptId: String, contentId: String) = contentDao.getContentWithId(attemptId, contentId)

    fun deleteNoteByID(id: String) = notesDao.deleteNoteByID(id)

    fun updateNote(notes: NotesModel) = viewModelScope.launch(Dispatchers.IO) { notesDao.update(notes) }

    fun updateDoubtStatus(uid: String, status: String) = doubtsDao.updateStatus(uid, status)

    fun getNotes(ruid: String) = notesDao.getNotes(ruid)

    fun getDoubts(ruid: String) = doubtsDao.getDoubts(ruid)

    fun getOtp(videoId: String, sectionId: String, attemptId: String) {
        Clients.api.getOtp(videoId, sectionId, attemptId)
            .enqueue(retrofitCallback { error, response ->
                response?.let {
                    mOtp = response.body()?.get("otp")?.asString
                    mPlaybackInfo = response.body()?.get("playbackInfo")?.asString
                    getOtpProgress.postValue(it.isSuccessful)
                    it.errorBody()?.let {
                        Crashlytics.log("Error Fetching Otp: ${it.string()}")
                    }
                }
                error?.let {
                    Crashlytics.log("Error Fetching Otp: ${it.message}")
                }
            })
    }

    fun createDoubt(doubt: Doubts, attemptId: String) {
        Clients.onlineV2JsonApi.createDoubt(doubt)
            .enqueue(retrofitCallback { _, response ->
                try {
                    if ((response?.isSuccessful == true))
                        response.body()?.let {
                            viewModelScope.launch(Dispatchers.IO) {
                                doubtsDao.insert(
                                    DoubtsModel(
                                        it.id, it.title, it.body, it.content?.id
                                        ?: "", it.status, attemptId
                                    )
                                )
                            }
                        }
                } catch (e: Exception) {
                    createDoubtProgress.value = false
                }
                createDoubtProgress.value = (response?.isSuccessful == true)
            })
    }

    fun createNote(note: Notes, attemptId: String) {
        Clients.onlineV2JsonApi.createNote(note)
            .enqueue(retrofitCallback { _, response ->
                response?.let { responseNote ->
                    createNoteProgress.value = true
                    if (responseNote.isSuccessful)
                        responseNote.body().let {
                            try {
                                viewModelScope.launch(Dispatchers.IO) {
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
                                }
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
        Clients.onlineV2JsonApi.getDoubtByAttemptId(param).enqueue(retrofitCallback { throwable, response ->
            response?.body().let { doubts ->
                if (response != null && response.isSuccessful) {
                    doubts?.forEach {
                        try {
                            viewModelScope.launch(Dispatchers.IO) {
                                doubtsDao.insert(
                                    DoubtsModel(
                                        it.id, it.title, it.body, it.content?.id
                                        ?: "", it.status, it.runAttempt?.id ?: "",
                                        it.discourseTopicId
                                    )
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Log.e("CRASH", "DOUBT ID : $it.id")
                        }
                    }
                }
            }
        })
    }

    fun fetchNotes(param: String) {
        val networkList: ArrayList<NotesModel> = ArrayList()
        Clients.onlineV2JsonApi.getNotesByAttemptId(param).enqueue(retrofitCallback { _, response ->
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
                            Log.i("Error", "error" + e.message)
                        }
                    }
                    if (networkList.size == notesList?.size) {
                        viewModelScope.launch(Dispatchers.IO) { notesDao.insertAll(networkList) }
                        notesDao.getNotes(param).observeOnce { list ->
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

    fun getNextVideo(contentId: String, sectionId: String, attemptId: String) = contentDao.getNextItem(sectionId, attemptId, contentId)

    fun deleteVideo(contentId: String) =
        viewModelScope.launch { contentDao.updateContent(contentId, 0) }
}
