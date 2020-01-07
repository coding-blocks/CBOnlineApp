package com.codingblocks.cbonlineapp.mycourse.quiz

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.models.QuizAttempt
import com.codingblocks.onlineapi.models.Quizzes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuizViewModel(private val repo: QuizRepository) : ViewModel() {

    var attemptId: String = ""
    var sectionId: String = ""
    var contentId: String = ""

    var bottomSheetQuizData: MutableLiveData<MutableList<MutableLiveData<Boolean>>> = MutableLiveData()
    val quizDetails = MutableLiveData<Quizzes>()
    val quizAttempts = MutableLiveData<List<QuizAttempt>>()

    fun fetchQuiz() {
        runIO {
            val quiz = withContext(Dispatchers.IO) { repo.getContent(contentId) }
            when (val response = quiz?.qnaQid?.let { repo.getQuizDetails(quizId = it.toString()) }) {
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

    private fun setError(error: String) {
    }
}
