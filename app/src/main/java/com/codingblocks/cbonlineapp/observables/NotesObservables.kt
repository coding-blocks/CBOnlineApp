package com.codingblocks.cbonlineapp.observables

import androidx.databinding.BaseObservable
import androidx.databinding.ObservableField

class NotesObservables : BaseObservable() {
    var editTvText: ArrayList<ObservableField<String>> = ArrayList()
    var deleteTvText: ArrayList<ObservableField<String>> = ArrayList()
    var bodyTvEnabled: ArrayList<ObservableField<Boolean>> = ArrayList()
    var bodyTvText: ArrayList<ObservableField<String>> = ArrayList()
}
