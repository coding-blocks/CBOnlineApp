package com.codingblocks.cbonlineapp.mycourse.player

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.dashboard.doubts.DashboardDoubtsRepository
import com.codingblocks.cbonlineapp.database.models.DoubtsModel
import com.codingblocks.cbonlineapp.util.LIVE
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError

class VideoPlayerViewModel(
    private val repo: VideoPlayerRepository,
    private val repoDoubts: DashboardDoubtsRepository
) : ViewModel() {
    var playWhenReady = false
    var currentOrientation: Int = 0
    var mOtp: String? = null
    var mPlaybackInfo: String? = null
    var attemptId: String = ""
    var sectionId: String = ""
    var videoId: String = ""
    var contentId: String = ""
    var getOtpProgress: MutableLiveData<Boolean> = MutableLiveData()
    val doubts by lazy {
        repoDoubts.getDoubtsByCourseRun(LIVE, attemptId)
    }
    val notes by lazy {
        repo.getNotes(attemptId)
    }

//    fun getRunByAtemptId(id: String) = runDao.getRunByAtemptId(id)
//
//    fun getCourseById(id: String) = courseDao.getCourses().value!![0]
//
//    fun getContentWithId(attemptId: String, contentId: String) = contentDao.getContentWithId(attemptId, contentId)
//
//    fun deleteNoteByID(id: String) = notesDao.deleteNoteByID(id)
//
//    fun updateNote(notes: NotesModel) = viewModelScope.launch(Dispatchers.IO) { notesDao.update(notes) }
//
//    fun updateDoubtStatus(uid: String, status: String) = doubtsDao.updateStatus(uid, status)
//
//    fun getNotes(ruid: String) = notesDao.getNotes(ruid)
//
//    fun getDoubts(ruid: String) = doubtsDao.getDoubts(ruid)
//
//    fun getOtp(videoId: String, sectionId: String, attemptId: String) {
//        Clients.api.getOtp(videoId, sectionId, attemptId)
//            .enqueue(retrofitCallback { error, response ->
//                response?.let {
//                    mOtp = response.body()?.get("otp")?.asString
//                    mPlaybackInfo = response.body()?.get("playbackInfo")?.asString
//                    getOtpProgress.postValue(it.isSuccessful)
//                    it.errorBody()?.let {
//                        Crashlytics.log("Error Fetching Otp: ${it.string()}")
//                    }
//                }
//                error?.let {
//                    Crashlytics.log("Error Fetching Otp: ${it.message}")
//                }
//            })
//    }
//
//    fun createDoubt(doubt: Doubts, attemptId: String) {
//        Clients.onlineV2JsonApi.createDoubt(doubt)
//            .enqueue(retrofitCallback { _, response ->
//                try {
//                    if ((response?.isSuccessful == true))
//                        response.body()?.let {
//                            viewModelScope.launch(Dispatchers.IO) {
//                                //                                doubtsDao.insert(
////                                    DoubtsModel(
////                                        it.id, it.title, it.body, it.content?.id
////                                        ?: "", it.status, attemptId
////                                    )
////                                )
//                            }
//                        }
//                } catch (e: Exception) {
//                    createDoubtProgress.value = false
//                }
//                createDoubtProgress.value = (response?.isSuccessful == true)
//            })
//    }
//
//    fun createNote(note: Notes, attemptId: String) {
//        Clients.onlineV2JsonApi.createNote(note)
//            .enqueue(retrofitCallback { _, response ->
//                response?.let { responseNote ->
//                    createNoteProgress.value = true
//                    if (responseNote.isSuccessful)
//                        responseNote.body().let {
//                            try {
//                                viewModelScope.launch(Dispatchers.IO) {
//                                    notesDao.insert(
//                                        NotesModel(
//                                            it?.id ?: "",
//                                            it?.duration ?: 0.0,
//                                            it?.text ?: "",
//                                            it?.content?.id ?: "",
//                                            attemptId,
//                                            it?.createdAt ?: "",
//                                            it?.deletedAt ?: ""
//                                        )
//                                    )
//                                }
//                            } catch (e: Exception) {
//                                createNoteProgress.value = false
//                            }
//                        }
//                    else
//                        createNoteProgress.value = false
//                }
//            })
//    }

//    fun fetchDoubts(param: String) {
//        Clients.onlineV2JsonApi.getDoubtByAttemptId(param).enqueue(retrofitCallback { throwable, response ->
//            response?.body().let { doubts ->
//                if (response != null && response.isSuccessful) {
//                    doubts?.forEach {
//                        try {
//                            viewModelScope.launch(Dispatchers.IO) {
//                                doubtsDao.insert(
//                                    DoubtsModel(
//                                        it.id, it.title, it.body, it.content?.id
//                                        ?: "", it.status, it.runAttempt?.id ?: "",
//                                        it.discourseTopicId
//                                    )
//                                )
//                            }
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                            Log.e("CRASH", "DOUBT ID : $it.id")
//                        }
//                    }
//                }
//            }
//        })
//    }

    fun fetchNotes(param: String) {
//        val networkList: ArrayList<NotesModel> = ArrayList()
//        Clients.onlineV2JsonApi.getNotesByAttemptId(param).enqueue(retrofitCallback { _, response ->
//            response?.body().let { notesList ->
//                if (response?.isSuccessful == true) {
//                    notesList?.forEach {
//                        try {
//                            networkList.add(
//                                NotesModel(
//                                    it.id,
//                                    it.duration ?: 0.0,
//                                    it.text ?: "",
//                                    it.content?.id
//                                        ?: "",
//                                    it.runAttempt?.id ?: "",
//                                    it.createdAt ?: "",
//                                    it.deletedAt
//                                        ?: ""
//                                )
//                            )
//                        } catch (e: Exception) {
//                            Log.i("Error", "error" + e.message)
//                        }
//                    }
//                    if (networkList.size == notesList?.size) {
//                        viewModelScope.launch(Dispatchers.IO) { notesDao.insertAll(networkList) }
//                        notesDao.getNotes(param).observeOnce { list ->
//                            // remove items which are deleted
//                            val sum = list + networkList
//                            sum.groupBy { it.nttUid }
//                                .filter { it.value.size == 1 }
//                                .flatMap { it.value }
//                                .forEach {
//                                    notesDao.deleteNoteByID(it.nttUid)
//                                }
//                        }
//                    }
//                }
//            }
//        })
    }

    fun resolveDoubt(doubt: DoubtsModel) {
        runIO {
            when (val response = repoDoubts.resolveDoubt(doubt)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful) {
                        fetchDoubts()
                    } else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

    fun fetchDoubts() {
        runIO {
            when (val response = repoDoubts.fetchDoubtsByCourseRun(attemptId)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        response.value.body()?.let {
                            repoDoubts.insertDoubts(it)
                        }
                    else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

    fun fetchNotes() {
        runIO {
            when (val response = repo.fetchCourseNotes(attemptId)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        response.value.body()?.let { notes ->
                            repo.insertNotes(notes)
                        }
                    else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

    private fun setError(error: String) {
//        errorLiveData.postValue(error)
    }

//    fun getNextVideo(contentId: String, sectionId: String, attemptId: String) = contentDao.getNextItem(sectionId, attemptId, contentId)
//
//    fun deleteVideo(contentId: String) =
//        viewModelScope.launch { contentDao.updateContent(contentId, 0) }
}
