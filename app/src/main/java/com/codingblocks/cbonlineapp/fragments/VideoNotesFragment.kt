package com.codingblocks.cbonlineapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.adapters.VideosNotesAdapter
import com.codingblocks.cbonlineapp.database.models.NotesModel
import com.codingblocks.cbonlineapp.databinding.FragmentNotesBinding
import com.codingblocks.cbonlineapp.util.OnItemClickListener
import com.codingblocks.cbonlineapp.extensions.observer
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.viewmodels.VideoPlayerViewModel
import kotlinx.android.synthetic.main.fragment_notes.view.*
import org.jetbrains.anko.AnkoLogger
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class VideoNotesFragment : Fragment(), AnkoLogger {
    private var param1: String? = null

    private val notesViewModel by sharedViewModel<VideoPlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(RUN_ATTEMPT_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DataBindingUtil.inflate<FragmentNotesBinding>(inflater, R.layout.fragment_notes, container, false).apply {
            viewModel = notesViewModel
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notesViewModel.fetchNotes(param1 ?: "")

        notesViewModel.notesAdapter = VideosNotesAdapter(arrayListOf(), object : OnItemClickListener {
            override fun onItemClick(position: Int, id: String) {
                val baseActivity = activity
                if (baseActivity is OnItemClickListener)
                    baseActivity.onItemClick(position, id)
            }
        }, notesViewModel)

        notesViewModel.getNotes(param1 ?: "").observer(this) {
            notesViewModel.notesAdapter?.setData(arrayListOf<NotesModel>().apply { addAll(it) })
            view.notesRv.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
            view.emptyTv.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            VideoNotesFragment().apply {
                arguments = Bundle().apply {
                    putString(RUN_ATTEMPT_ID, param1)
                }
            }
    }
}
