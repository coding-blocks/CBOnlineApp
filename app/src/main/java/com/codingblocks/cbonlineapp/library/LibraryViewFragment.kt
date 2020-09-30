package com.codingblocks.cbonlineapp.library

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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
import com.codingblocks.cbonlineapp.database.models.NotesModel
import com.codingblocks.cbonlineapp.mycourse.MyCourseActivity
import com.codingblocks.cbonlineapp.mycourse.content.codechallenge.CodeChallengeActivity
import com.codingblocks.cbonlineapp.mycourse.content.document.PdfActivity
import com.codingblocks.cbonlineapp.mycourse.content.player.VideoPlayerActivity.Companion.createVideoPlayerActivityIntent
import com.codingblocks.cbonlineapp.mycourse.content.quiz.QuizActivity
import com.codingblocks.cbonlineapp.util.*
import com.codingblocks.cbonlineapp.util.FileUtils
import com.codingblocks.cbonlineapp.util.extensions.setRv
import com.codingblocks.cbonlineapp.util.extensions.showDialog
import com.codingblocks.cbonlineapp.util.widgets.ProgressDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.bottom_sheet_note.view.*
import kotlinx.android.synthetic.main.fragment_library_view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.io.File
import java.util.*

class LibraryViewFragment : BaseCBFragment() {

    private val vm by sharedViewModel<LibraryViewModel>()
    private lateinit var libraryListAdapter: LibraryListAdapter
    private var selectionTracker: SelectionTracker<String>? = null
    private val dialog: BottomSheetDialog by lazy { BottomSheetDialog(requireContext()) }
    private val sheetDialog: View by lazy { layoutInflater.inflate(R.layout.bottom_sheet_note, null) }
    private val itemClickListener: ItemClickListener by lazy {
        object : ItemClickListener {
            override fun onClick(item: BaseModel) {
                when (item) {
                    is ContentLecture -> startActivity(
                        createVideoPlayerActivityIntent(requireContext(), item.lectureContentId, item.lectureSectionId)
                    )
                    is BookmarkModel -> {
                        when (item.contentable) {
                            DOCUMENT ->
                                startActivity(
                                    intentFor<PdfActivity>(
                                        CONTENT_ID to item.contentId,
                                        SECTION_ID to item.sectionId
                                    )
                                )
                            LECTURE, VIDEO ->
                                startActivity(
                                    createVideoPlayerActivityIntent(requireContext(), item.contentId, item.sectionId)
                                )
                            QNA ->
                                startActivity(
                                    intentFor<QuizActivity>(
                                        CONTENT_ID to item.contentId,
                                        SECTION_ID to item.sectionId
                                    )
                                )
                            CODE ->
                                startActivity(
                                    intentFor<CodeChallengeActivity>(
                                        CONTENT_ID to item.contentId,
                                        SECTION_ID to item.sectionId
                                    )
                                )
                        }
                    }
                    is NotesModel -> updateNotes(item)
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
        setUpBottomSheet()
        typeTv.text = vm.type
        when (vm.type) {
            getString(R.string.notes) -> {
                libraryListAdapter = LibraryListAdapter(LibraryTypes.NOTE)
                vm.fetchNotes().observe(thisLifecycleOwner) {
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
                vm.fetchBookmarks().observe(thisLifecycleOwner) {
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
                vm.fetchDownloads().observe(thisLifecycleOwner) {
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
            startActivity(
                MyCourseActivity.createMyCourseActivityIntent(
                    requireContext(),
                    vm.attemptId!!,
                    vm.name!!
                )
            )
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
            withContext(Dispatchers.IO) { FileUtils.deleteRecursive(dir) }
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

    private fun setUpBottomSheet() {
        dialog.dismissWithAnimation = true
        dialog.setContentView(sheetDialog)
        Objects.requireNonNull(dialog.window)?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val sheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)!!
            BottomSheetBehavior.from(sheet).setState(BottomSheetBehavior.STATE_EXPANDED)
        }
    }

    private fun updateNotes(item: NotesModel) {
        sheetDialog.apply {
            bottomSheetTitleTv.text = getString(R.string.add_note)
            doubtTitleTv.isVisible = false
            bottoSheetDescTv.setText(item.text)
            bottomSheetInfoTv.text = "${item.contentTitle}"
            bottomSheetCancelBtn.setOnClickListener {
                dialog.dismiss()
            }
            bottomSheetSaveBtn.setOnClickListener {
                val desc = sheetDialog.bottoSheetDescTv.text.toString()
                when {
                    desc.isEmpty() -> {
                        toast("Note cannot be empty!!")
                    }
                    desc == item.text -> {
                        toast("Same note cannot be added.")
                    }
                    else -> {
                        vm.updateNote(item.apply { text = sheetDialog.bottoSheetDescTv.text.toString() })
                        dialog.dismiss()
                    }
                }
            }
        }
        dialog.show()
    }
}
