package com.codingblocks.cbonlineapp.quiz

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class QuizViewModel : ViewModel() {
    var bottomSheetQuizData: MutableLiveData<MutableList<MutableLiveData<Boolean>>> = MutableLiveData()
}
