package com.codingblocks.cbonlineapp.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.database.models.BaseModel
import com.codingblocks.cbonlineapp.database.models.BookmarkModel
import com.codingblocks.cbonlineapp.database.models.ContentLecture
import com.codingblocks.cbonlineapp.database.models.LibraryTypes
import com.codingblocks.cbonlineapp.mycourse.player.VideoPlayerActivity
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.SECTION_ID
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.setRv
import kotlinx.android.synthetic.main.fragment_library_view.*
import org.jetbrains.anko.singleTop
import org.jetbrains.anko.support.v4.intentFor
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LibraryViewFragment : Fragment() {

    private val vm by sharedViewModel<LibraryViewModel>()
    private lateinit var libraryListAdapter: LibraryListAdapter
    private var selectionTracker: SelectionTracker<String>? = null
    private val itemClickListener: ItemClickListener by lazy {
        object : ItemClickListener {
            override fun onClick(item: BaseModel) {
                when (item) {
                    is ContentLecture -> startActivity(intentFor<VideoPlayerActivity>(CONTENT_ID to item.lectureContentId, SECTION_ID to item.lectureSectionId).singleTop())
                    is BookmarkModel -> startActivity(intentFor<VideoPlayerActivity>(CONTENT_ID to item.contentId, SECTION_ID to item.sectionId).singleTop())
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
        View? = inflater.inflate(R.layout.fragment_library_view, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        typeTv.text = vm.type
        when (vm.type) {
            getString(R.string.notes) -> {
                libraryListAdapter = LibraryListAdapter(LibraryTypes.NOTE)
                vm.fetchNotes().observer(this) {
                    libraryListAdapter.submitList(it)
                    hideRecyclerView(it.isNotEmpty())
                }
            }
            getString(R.string.announcements) -> vm.fetchNotes()
            getString(R.string.bookmarks) -> {
                libraryListAdapter = LibraryListAdapter(LibraryTypes.BOOKMARK)
                vm.fetchBookmarks().observer(this) {
                    libraryListAdapter.submitList(it)
                    hideRecyclerView(it.isNotEmpty())
                }
            }
            getString(R.string.downloads) -> {
                libraryListAdapter = LibraryListAdapter(LibraryTypes.DOWNLOADS)
                vm.fetchDownloads().observer(this) {
                    libraryListAdapter.submitList(it)
                    hideRecyclerView(it.isNotEmpty())
                }
            }
        }
        libraryRv.setRv(requireContext(), libraryListAdapter, true)

        selectionTracker = SelectionTracker.Builder<String>(
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
//                    deleteContainer.isVisible = items > 0
                }
            })
        classRoomBtn.setOnClickListener {
            //            browse(url)
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
                libEmptyImg.setImageResource(R.drawable.ic_bookmark)
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
