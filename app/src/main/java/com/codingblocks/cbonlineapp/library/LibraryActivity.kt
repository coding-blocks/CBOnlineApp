package com.codingblocks.cbonlineapp.library

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.dashboard.doubts.MyItemDetailsLookup
import com.codingblocks.cbonlineapp.dashboard.doubts.MyItemKeyProvider
import com.codingblocks.cbonlineapp.util.COURSE_NAME
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.TYPE
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.setRv
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import kotlinx.android.synthetic.main.activity_library.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LibraryActivity : AppCompatActivity() {

    val viewModel by viewModel<LibraryViewModel>()
    val type by lazy {
        intent.getStringExtra(TYPE) ?: ""
    }
    private val notesListAdapter = LibraryNotesListAdapter()
    private var tracker: SelectionTracker<Long>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)
        setToolbar(libraryToolbar)
        libraryRv.setRv(this, notesListAdapter)
        title = intent.getStringExtra(COURSE_NAME)
        typeTv.text = type
        viewModel.attemptId = intent.getStringExtra(RUN_ATTEMPT_ID) ?: ""
        when (type) {
            getString(R.string.notes) -> viewModel.fetchNotes()
            getString(R.string.announcements) -> viewModel.fetchNotes()
            getString(R.string.bookmarks) -> viewModel.fetchNotes()
            getString(R.string.downloads) -> viewModel.fetchNotes()
        }

        tracker = SelectionTracker.Builder<Long>(
            "mySelection",
            libraryRv,
            MyItemKeyProvider(libraryRv),
            MyItemDetailsLookup(libraryRv),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        notesListAdapter.tracker = tracker

        tracker?.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    val items = tracker?.selection!!.size()
                    tracker?.selection!!.forEach {
                        Log.i("Selection Keys", "Note ID : $it")
                    }
                }
            })

        viewModel.notes.observer(this) { notes ->
            notesListAdapter.submitList(notes)
        }
    }
}
