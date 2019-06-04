package com.codingblocks.cbonlineapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.adapters.VideosDoubtsAdapter
import com.codingblocks.cbonlineapp.database.models.DoubtsModel
import com.codingblocks.cbonlineapp.extensions.observer
import com.codingblocks.cbonlineapp.viewmodels.VideoPlayerViewModel
import com.codingblocks.cbonlineapp.util.ARG_ATTEMPT_ID
import kotlinx.android.synthetic.main.fragment_video_doubt.view.*
import org.jetbrains.anko.AnkoLogger
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class VideoDoubtFragment : Fragment(), AnkoLogger {
    private var param1: String? = null

    private val viewModel by sharedViewModel<VideoPlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_ATTEMPT_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_video_doubt, container, false)
        viewModel.fetchDoubts(param1 ?: "")
        val doubtList = ArrayList<DoubtsModel>()
        val doubtsAdapter = VideosDoubtsAdapter(doubtList, viewModel)
        view.doubtsRv.layoutManager = LinearLayoutManager(requireContext())
        view.doubtsRv.adapter = doubtsAdapter
        val itemDecorator = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(requireContext().resources.getDrawable(R.drawable.divider))
        view.doubtsRv.addItemDecoration(itemDecorator)

        viewModel.getDoubts(param1 ?: "").observer(viewLifecycleOwner) {
            doubtsAdapter.setData(it as ArrayList<DoubtsModel>)
            view.doubtsRv.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
            view.emptyTv.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            VideoDoubtFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ATTEMPT_ID, param1)
                }
            }
    }
}
