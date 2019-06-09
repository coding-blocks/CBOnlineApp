package com.codingblocks.cbonlineapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.database.models.NotesModel
import com.codingblocks.cbonlineapp.databinding.FragmentNotesBinding
import com.codingblocks.cbonlineapp.extensions.observer
import com.codingblocks.cbonlineapp.viewmodels.VideoPlayerViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_notes.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class VideoNotesFragment : Fragment(), AnkoLogger {
    private val viewModel by sharedViewModel<VideoPlayerViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentNotesBinding>(inflater, R.layout.fragment_notes, container, false)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getNotes().observer(viewLifecycleOwner) {
            viewModel.getAdapter().setData(it as ArrayList<NotesModel>)
            view.notesRv.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
            view.emptyTv.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }
        viewModel.fetchNotes()

        viewModel.updatedNote.observer(viewLifecycleOwner) {
            if (it) toast("Updated Note Successfully")
            else toast("Error Updating Note")
        }

        viewModel.deleteNote.observer(viewLifecycleOwner) { pos ->
            view.snackbar("Deleted Accidentally ??", "Undo") {
                viewModel.getAdapter().notesData.add(pos, viewModel.deletedNote)
                viewModel.getAdapter().notifyItemInserted(pos)
            }.addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    if (event == DISMISS_EVENT_TIMEOUT) {
                        viewModel.deleteNoteById()
                    }
                }
            }).setActionTextColor(requireContext().resources.getColor(R.color.salmon))
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = VideoNotesFragment()
    }
}
