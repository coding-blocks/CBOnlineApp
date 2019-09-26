package com.codingblocks.cbonlineapp.mycourse.doubts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.ARG_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.COURSE_ID
import com.codingblocks.cbonlineapp.util.extensions.retrofitCallback
import com.codingblocks.onlineapi.Clients
import kotlinx.android.synthetic.main.fragment_doubts.*
import org.jetbrains.anko.AnkoLogger

class DoubtsFragment : Fragment(), AnkoLogger {

    private val attemptId: String by lazy {
        arguments?.getString(ARG_ATTEMPT_ID) ?: ""
    }
    private val courseId: String by lazy {
        arguments?.getString(COURSE_ID) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
        View? = inflater.inflate(R.layout.fragment_doubts, container, false).apply {
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val doubtsAdapter = DoubtsAdapter(ArrayList())
        doubtsRv.layoutManager = LinearLayoutManager(context)
        doubtsRv.adapter = doubtsAdapter
        val itemDecorator = DividerItemDecoration(context!!, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(ContextCompat.getDrawable(context!!, R.drawable.divider_black)!!)
        doubtsRv.addItemDecoration(itemDecorator)
        Clients.api.getDoubts(courseId).enqueue(retrofitCallback { _, doubtsresponse ->
            doubtsresponse?.body().let {
                it?.topicList?.topics?.let { it1 -> doubtsAdapter.setData(it1) }
            }
        })
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, crUid: String) =
            DoubtsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ATTEMPT_ID, param1)
                    putString(COURSE_ID, crUid)
                }
            }
    }
}
