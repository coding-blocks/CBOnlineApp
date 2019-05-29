package com.codingblocks.cbonlineapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.cbonlineapp.adapters.VideosDoubtsAdapter
import com.codingblocks.cbonlineapp.database.AppDatabase
import com.codingblocks.cbonlineapp.database.models.DoubtsModel
import com.codingblocks.onlineapi.Clients
import kotlinx.android.synthetic.main.fragment_video_doubt.view.*
import org.jetbrains.anko.AnkoLogger

private const val ARG_ATTEMPT_ID = "param1"

class VideoDoubtFragment : Fragment(), AnkoLogger {
    private var param1: String? = null

    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(context!!)
    }

    private val doubtsDao by lazy {
        database.doubtsDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_ATTEMPT_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_video_doubt, container, false)
        fetchDoubts()
        val doubtList = ArrayList<DoubtsModel>()
        val doubtsAdapter = VideosDoubtsAdapter(doubtList)
        view.doubtsRv.layoutManager = LinearLayoutManager(context)
        view.doubtsRv.adapter = doubtsAdapter
        val itemDecorator = DividerItemDecoration(context!!, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(ContextCompat.getDrawable(context!!, R.drawable.divider)!!)
        view.doubtsRv.addItemDecoration(itemDecorator)
        doubtsDao.getDoubts(param1!!).observe(this, Observer<List<DoubtsModel>> {
            doubtsAdapter.setData(it as ArrayList<DoubtsModel>)
            view.doubtsRv.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
            view.emptyTv.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        })
        return view
    }

    private fun fetchDoubts() {
        Clients.onlineV2JsonApi.getDoubtByAttemptId(param1!!).enqueue(retrofitCallback { throwable, response ->
            response?.body().let {
                if (response != null && response.isSuccessful) {
                    it?.forEach {
                        try {
                            doubtsDao.insert(
                                DoubtsModel(
                                    it.id, it.title, it.body, it.content?.id
                                    ?: "", it.status, it.runAttempt?.id ?: "",
                                    it.discourseTopicId
                                )
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Log.e("CRASH", "DOUBT ID : $it.id")
                        }
                    }
                }
            }
        })
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
