package com.codingblocks.cbonlineapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.adapters.VideosNotesAdapter
import com.codingblocks.cbonlineapp.database.models.NotesModel
import com.codingblocks.cbonlineapp.util.OnItemClickListener
import com.codingblocks.cbonlineapp.extensions.observer
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.viewmodels.VideoPlayerViewModel
import kotlinx.android.synthetic.main.fragment_notes.view.*
import org.jetbrains.anko.AnkoLogger
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class VideoNotesFragment : Fragment(), AnkoLogger {
    private var param1: String? = null

    private val viewModel by sharedViewModel<VideoPlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(RUN_ATTEMPT_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notes, container, false)

        viewModel.fetchNotes(param1 ?: "")

        val notesList = ArrayList<NotesModel>()
        val notesAdapter = VideosNotesAdapter(notesList, object : OnItemClickListener {
            override fun onItemClick(position: Int, id: String) {
                try {
                    (activity as OnItemClickListener).onItemClick(position, id)
                } catch (cce: ClassCastException) {
                }
            }
        }, viewModel)
        view.notesRv.layoutManager = LinearLayoutManager(requireContext())
        view.notesRv.adapter = notesAdapter
        val itemDecorator = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(requireContext().resources.getDrawable(R.drawable.divider))
        view.notesRv.addItemDecoration(itemDecorator)

        viewModel.getNotes(param1 ?: "").observer(this) {
            notesAdapter.setData(it as ArrayList<NotesModel>)
            view.notesRv.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
            view.emptyTv.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }

        return view
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
