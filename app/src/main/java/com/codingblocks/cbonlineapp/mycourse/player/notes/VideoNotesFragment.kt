package com.codingblocks.cbonlineapp.mycourse.player.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.database.models.BaseModel
import com.codingblocks.cbonlineapp.database.models.LibraryTypes
import com.codingblocks.cbonlineapp.database.models.NotesModel
import com.codingblocks.cbonlineapp.library.DeleteNoteClickListener
import com.codingblocks.cbonlineapp.library.EditNoteClickListener
import com.codingblocks.cbonlineapp.library.ItemClickListener
import com.codingblocks.cbonlineapp.library.LibraryListAdapter
import com.codingblocks.cbonlineapp.mycourse.player.VideoPlayerViewModel
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.setRv
import com.codingblocks.cbonlineapp.util.extensions.showSnackbar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_notes.*
import org.jetbrains.anko.AnkoLogger
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class VideoNotesFragment : BaseCBFragment(), AnkoLogger {

    private val viewModel by sharedViewModel<VideoPlayerViewModel>()
    private val notesListAdapter = LibraryListAdapter(LibraryTypes.NOTESVIDEO)

    private val deleteClickListener: DeleteNoteClickListener by lazy {
        object : DeleteNoteClickListener {
            override fun onClick(noteId: String, position: Int, view: View) {
                view.isVisible = false
                videoNotesRoot.showSnackbar(
                    getString(R.string.noted_del_msg),
                    Snackbar.LENGTH_SHORT,
                    action = true,
                    actionText = "UNDO"
                ) {
                    view.isVisible = true
                }.addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        if (event == DISMISS_EVENT_TIMEOUT) {
                            viewModel.deleteNote(noteId)
                        }
                    }
                })
            }
        }
    }

    private val editClickListener: EditNoteClickListener by lazy {
        object : EditNoteClickListener {
            override fun onClick(note: NotesModel) {
                (activity as EditNoteClickListener).onClick(note)
            }
        }
    }

    private val itemClickListener: ItemClickListener by lazy {
        object : ItemClickListener {
            override fun onClick(model: BaseModel) {
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_notes, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerNotesRv.setRv(requireContext(), notesListAdapter, false)

        viewModel.notes.observer(viewLifecycleOwner) {
            notesListAdapter.submitList(it)
            showEmptyView(it.isNullOrEmpty())
        }

        notesListAdapter.apply {
            onDeleteClick = deleteClickListener
            onEditClick = editClickListener
            onItemClick = itemClickListener
        }
    }

    private fun showEmptyView(show: Boolean) {
        playerNotesRv.isVisible = !show
        noNotesLayout.isVisible = show
    }

    override fun onDestroy() {
        notesListAdapter.apply {
            onDeleteClick = null
            onEditClick = null
            onItemClick = null
        }
        super.onDestroy()
    }
}
