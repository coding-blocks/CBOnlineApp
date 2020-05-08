package com.codingblocks.cbonlineapp.library

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.database.models.BaseModel
import com.codingblocks.cbonlineapp.database.models.BookmarkModel
import com.codingblocks.cbonlineapp.database.models.ContentLecture
import com.codingblocks.cbonlineapp.database.models.LibraryTypes
import com.codingblocks.cbonlineapp.mycourse.MyCourseActivity
import com.codingblocks.cbonlineapp.mycourse.player.VideoPlayerActivity
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.MediaUtils
import com.codingblocks.cbonlineapp.util.SECTION_ID
import com.codingblocks.cbonlineapp.util.extensions.setRv
import com.codingblocks.cbonlineapp.util.extensions.showDialog
import com.codingblocks.cbonlineapp.util.widgets.ProgressDialog
import java.io.File
import kotlinx.android.synthetic.main.fragment_library_view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.singleTop
import org.jetbrains.anko.support.v4.intentFor
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LibraryViewFragment : BaseCBFragment() {

    private val vm by sharedViewModel<LibraryViewModel>()
    private lateinit var libraryListAdapter: LibraryListAdapter
    private var selectionTracker: SelectionTracker<String>? = null
    private val itemClickListener: ItemClickListener by lazy {
        object : ItemClickListener {
            override fun onClick(item: BaseModel) {
                when (item) {
                    is ContentLecture -> startActivity(
                        intentFor<VideoPlayerActivity>(
                            CONTENT_ID to item.lectureContentId,
                            SECTION_ID to item.lectureSectionId
                        ).singleTop()
                    )
                    is BookmarkModel -> startActivity(
                        intentFor<VideoPlayerActivity>(
                            CONTENT_ID to item.contentId,
                            SECTION_ID to item.sectionId
                        ).singleTop()
                    )
//                    is NotesModel -> startActivity(intentFor<VideoPlayerActivity>(CONTENT_ID to item.contentId, SECTION_ID to item.sectionId).singleTop())
                }
            }
        }
    }
    val progressDialog by lazy {
        ProgressDialog.progressDialog(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_library_view, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        typeTv.text = vm.type
        when (vm.type) {
            getString(R.string.notes) -> {
                libraryListAdapter = LibraryListAdapter(LibraryTypes.NOTE)
                vm.fetchNotes().observe(viewLifecycleOwner) {
                    if (it.isNullOrEmpty()) {
                        libraryListAdapter.submitList(emptyList())
                    } else {
                        libraryListAdapter.submitList(it)
                    }
                    hideRecyclerView(it.isNotEmpty())
                }
            }
            getString(R.string.announcements) -> vm.fetchNotes()
            getString(R.string.bookmarks) -> {
                libraryListAdapter = LibraryListAdapter(LibraryTypes.BOOKMARK)
                vm.fetchBookmarks().observe(viewLifecycleOwner) {
                    if (it.isNullOrEmpty()) {
                        libraryListAdapter.submitList(emptyList())
                    } else {
                        libraryListAdapter.submitList(it)
                    }
                    hideRecyclerView(it.isNotEmpty())
                }
            }
            else -> {
                libraryListAdapter = LibraryListAdapter(LibraryTypes.DOWNLOADS)
                vm.fetchDownloads().observe(viewLifecycleOwner) {
                    if (it.isNullOrEmpty()) {
                        libraryListAdapter.submitList(emptyList())
                    } else {
                        libraryListAdapter.submitList(it)
                    }
                    downloadTv.isVisible = it.isNotEmpty()
                    hideRecyclerView(it.isNotEmpty())
                }
            }
        }
        libraryRv.setRv(requireContext(), libraryListAdapter, true)

        selectionTracker = SelectionTracker.Builder(
            "mySelection",
            libraryRv,
            MyItemKeyProvider(libraryListAdapter),
            MyItemDetailsLookup(libraryRv),
            StorageStrategy.createStringStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        libraryListAdapter.apply {
            onItemClick = itemClickListener
            this.tracker = selectionTracker
        }
        selectionTracker?.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    val items = selectionTracker?.selection!!.size()
                    deleteContainer.isVisible = items > 0
                    deleteTv.text = "Delete $items ${vm.type}"
                }
            })
        classRoomBtn.setOnClickListener {
            startActivity(MyCourseActivity.createMyCourseActivityIntent(
                requireContext(),
                vm.attemptId!!,
                vm.name!!
            ))
        }

        deleteAction.setOnClickListener {
            requireContext().showDialog(
                "DELETE",
                true,
                R.drawable.ic_delete,
                secondaryText = R.string.delete_confrim,
                primaryButtonText = R.string.confirmation
            ) {

                when (vm.type) {
                    getString(R.string.notes) -> {
                        selectionTracker?.selection?.forEach {
                            vm.deleteNote(it)
                        }.also { deleteContainer.isVisible = false }
                    }
                    getString(R.string.bookmarks) -> {
                        selectionTracker?.selection?.forEach {
                            vm.removeBookmark(it)
                        }.also { deleteContainer.isVisible = false }
                    }
                    getString(R.string.downloads) -> {
                        selectionTracker?.selection?.forEach {
                            deleteFolder(it)
                        }.also { deleteContainer.isVisible = false }
                    }
                }
            }
        }

        closeDelete.setOnClickListener {
            selectionTracker?.clearSelection()
            deleteContainer.isVisible = false
        }
    }

    private fun deleteFolder(lectureId: String) {
        val dir = File(
            requireContext().getExternalFilesDir(Environment.getDataDirectory().absolutePath),
            lectureId
        )
        GlobalScope.launch(Dispatchers.Main) {
            progressDialog.show()
            withContext(Dispatchers.IO) { MediaUtils.deleteRecursive(dir) }
            delay(3000)
            vm.updateDownload(0, lectureId)
            progressDialog.dismiss()
        }
    }

    override fun onDestroy() {
        libraryListAdapter.apply {
            onItemClick = null
        }
        super.onDestroy()
    }

    private fun hideRecyclerView(notEmpty: Boolean) {
        libraryRv.isVisible = notEmpty
        emptyLl.isVisible = !notEmpty
        when (vm.type) {
            getString(R.string.notes) -> {
                libEmptyImg.setImageResource(R.drawable.ic_note_big)
                libEmptyMessageTv.text = getString(R.string.empty_notes_title)
                libEmptyDescriptionTv.text = getString(R.string.empty_notes_text)
            }
            getString(R.string.bookmarks) -> {
                libEmptyImg.setImageResource(R.drawable.ic_bookmark_big)
                libEmptyMessageTv.text = getString(R.string.empty_bookmark_title)
                libEmptyDescriptionTv.text = getString(R.string.empty_bookmark_text)
            }
            getString(R.string.downloads) -> {
                libEmptyImg.setImageResource(R.drawable.ic_download_big)
                libEmptyMessageTv.text = getString(R.string.empty_download_title)
                libEmptyDescriptionTv.text = getString(R.string.empty_download_text)
            }
        }
    }
}
