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
import com.codingblocks.cbonlineapp.database.models.LibraryTypes
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
    private lateinit var libraryListAdapter: LibraryListAdapter
    private var tracker: SelectionTracker<Long>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)
        setToolbar(libraryToolbar)
        title = intent.getStringExtra(COURSE_NAME)
        typeTv.text = type
        viewModel.attemptId = intent.getStringExtra(RUN_ATTEMPT_ID) ?: ""
        when (type) {
            getString(R.string.notes) -> {
                libraryListAdapter = LibraryListAdapter(LibraryTypes.NOTE)
                viewModel.fetchNotes().observer(this) {
                    libraryListAdapter.submitList(it)
                }
            }
            getString(R.string.announcements) -> viewModel.fetchNotes()
            getString(R.string.bookmarks) -> {
                libraryListAdapter = LibraryListAdapter(LibraryTypes.BOOKMARK)
                viewModel.fetchBookmarks().observer(this) {
                    libraryListAdapter.submitList(it)
                }
            }
            getString(R.string.downloads) -> viewModel.fetchNotes()
        }
        libraryRv.setRv(this, libraryListAdapter)

        tracker = SelectionTracker.Builder<Long>(
            "mySelection",
            libraryRv,
            MyItemKeyProvider(libraryRv),
            MyItemDetailsLookup(libraryRv),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        libraryListAdapter.tracker = tracker

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
    }
}
