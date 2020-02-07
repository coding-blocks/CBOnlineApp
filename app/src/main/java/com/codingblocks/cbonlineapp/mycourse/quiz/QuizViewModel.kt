package com.codingblocks.cbonlineapp.mycourse.quiz

import androidx.lifecycle.MutableLiveData
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import com.codingblocks.cbonlineapp.database.models.ContentQnaModel
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.models.ContentQna
import com.codingblocks.onlineapi.models.QuizAttempt
import com.codingblocks.onlineapi.models.Quizzes
import com.codingblocks.onlineapi.models.RunAttempts

class QuizViewModel(private val repo: QuizRepository) : BaseCBViewModel() {

    var attemptId: String = ""
    var sectionId: String = ""
    var contentId: String = ""
    var quizAttemptId: String = ""
    lateinit var quiz: ContentQnaModel

    var bottomSheetQuizData: MutableLiveData<MutableList<MutableLiveData<Boolean>>> = MutableLiveData()
    val quizDetails = MutableLiveData<Quizzes>()
    val quizAttempts = MutableLiveData<List<QuizAttempt>>()
    val quizAttempt = MutableLiveData<QuizAttempt>()
    val content by lazy {
        repo.getContent(contentId)
    }

    fun fetchQuiz() {
        runIO {
            when (val response = quiz.qnaQid.let { repo.getQuizDetails(quizId = it.toString()) }) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful) {
                        response.value.body()?.let {
                            quizDetails.postValue(it)
                        }
                    } else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
            when (val response = quiz?.qnaUid?.let { repo.getQuizAttempts(qnaId = it) }) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful) {
                        response.value.body()?.let {
                            quizAttempts.postValue(it)
                        }
                    } else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

    fun startQuiz(function: () -> Unit) {
        runIO {
            val quizAttempt = QuizAttempt(ContentQna(quiz.qnaUid), RunAttempts(attemptId))
            when (val response = repo.createQuizAttempt(quizAttempt)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful) {
                        response.value.body()?.let {
                            quizAttemptId = it.id
                            function()
                        }
                    } else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

    fun getQuizAttempt() {
        runIO {
            when (val response = repo.fetchQuizAttempt(quizAttemptId)) {
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
            when (val response = repo.submitQuiz(quizAttemptId)) {
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

    fun getContentModel(contentId: String) {
    }
}
