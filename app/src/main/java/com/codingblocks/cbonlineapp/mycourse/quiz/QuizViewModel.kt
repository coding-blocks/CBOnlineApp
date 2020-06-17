package com.codingblocks.cbonlineapp.mycourse.quiz

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.liveData
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.QUIZ_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.QUIZ_ID
import com.codingblocks.cbonlineapp.util.QUIZ_QNA
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.SECTION_ID
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.cbonlineapp.util.extensions.savedStateValue
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.models.ContentQna
import com.codingblocks.onlineapi.models.QuizAttempt
import com.codingblocks.onlineapi.models.RunAttempts
import kotlinx.coroutines.Dispatchers

class QuizViewModel(
    handle: SavedStateHandle,
    private val repo: QuizRepository
) : BaseCBViewModel() {

    var sectionId by savedStateValue<String>(handle, SECTION_ID)
    var contentId by savedStateValue<String>(handle, CONTENT_ID)

    var attemptId by savedStateValue<String>(handle, RUN_ATTEMPT_ID)

    var quizQuestionId by savedStateValue<String>(handle, QUIZ_QNA)
    var quizId by savedStateValue<String>(handle, QUIZ_ID)


    var quizAttemptId by savedStateValue<String>(handle, QUIZ_ATTEMPT_ID)

    var bottomSheetQuizData: MutableLiveData<MutableList<MutableLiveData<Boolean>>> = MutableLiveData()
    val quizAttempt = MutableLiveData<QuizAttempt>()
    val content by lazy {
        repo.getContent(contentId!!)
    }

    fun fetchQuiz() = liveData(Dispatchers.IO)
    {
        when (val response = repo.getQuizDetails(quizQuestionId!!)) {
            is ResultWrapper.GenericError -> setError(response.error)
            is ResultWrapper.Success -> {
                if (response.value.isSuccessful) {
                    response.value.body()?.let {
                        emit(it)
                    }
                } else {
                    setError(fetchError(response.value.code()))
                }
            }
        }
    }

    fun fetchQuizAttempts() = liveData(Dispatchers.IO) {
        when (val response = repo.getQuizAttempts(quizId!!)) {
            is ResultWrapper.GenericError -> setError(response.error)
            is ResultWrapper.Success -> {
                if (response.value.isSuccessful) {
                    response.value.body()?.let {
                        emit(it)
                    }
                } else {
                    setError(fetchError(response.value.code()))
                }
            }
        }
    }

    fun startQuiz() = liveData(Dispatchers.IO) {
        val quizAttempt = QuizAttempt(ContentQna(quizId!!), RunAttempts(attemptId!!))
        when (val response = repo.createQuizAttempt(quizAttempt)) {
            is ResultWrapper.GenericError -> setError(response.error)
            is ResultWrapper.Success -> {
                if (response.value.isSuccessful) {
                    response.value.body()?.let {
                        quizAttemptId = it.id
                        emit(true)
                    }
                } else {
                    setError(fetchError(response.value.code()))
                }
            }
        }
    }

    fun getQuizAttempt() {
        runIO {
            when (val response = repo.fetchQuizAttempt(quizAttemptId!!)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful) {
                        response.value.body()?.let {
                            quizAttempt.postValue(it)
                        }
                    } else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

    fun submitQuiz(function: () -> Unit) {
        runIO {
            when (val response = repo.submitQuiz(quizAttemptId!!)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful) {
                        function()
                    } else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }
}
