package com.codingblocks.cbonlineapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.retrofitCallback
import com.codingblocks.cbonlineapp.adapters.DoubtsAdapter
import com.codingblocks.cbonlineapp.database.AppDatabase
import com.codingblocks.cbonlineapp.database.DoubtsModel
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Contents
import com.codingblocks.onlineapi.models.DoubtsJsonApi
import com.codingblocks.onlineapi.models.QuizRunAttempt
import kotlinx.android.synthetic.main.doubt_dialog.view.*
import kotlinx.android.synthetic.main.fragment_video_doubt.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.layoutInflater
import kotlin.concurrent.thread


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class VideoDoubtFragment : Fragment(), AnkoLogger {
    private var param1: String? = null
    private var param2: String? = null

    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(context!!)
    }

    private val doubtsDao by lazy {
        database.doubtsDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        info { "params$param1$param2" }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_video_doubt, container, false)

        fetchDoubts()

        view.doubtFab.setOnClickListener {
            showDialog()
        }

        val doubtList = ArrayList<DoubtsModel>()
        val doubtsAdapter = DoubtsAdapter(doubtList)
        view.doubtsRv.layoutManager = LinearLayoutManager(context)
        view.doubtsRv.adapter = doubtsAdapter
        doubtsDao.getDoubts(param1!!).observe(this, Observer<List<DoubtsModel>> {
            doubtsAdapter.setData(it as ArrayList<DoubtsModel>)
            if(it.isEmpty()){
                view.doubtsRv.visibility = View.GONE
                view.emptyTv.visibility = View.VISIBLE
            }else{
                view.doubtsRv.visibility = View.VISIBLE
                view.emptyTv.visibility = View.GONE
            }
        })


        return view
    }

    private fun fetchDoubts() {
        Clients.onlineV2JsonApi.getDoubtByAttemptId(param1!!).enqueue(retrofitCallback { throwable, response ->
            response?.body().let {
                if (response!!.isSuccessful) {
                    it?.forEach {
                        try {
                            info { "rundid" + it.runAttempt?.id }
                            doubtsDao.insert(DoubtsModel(it.id
                                    ?: "", it.title, it.body, it.content?.id
                                    ?: "", it.status, it.runAttempt?.id ?: ""
                            ))
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Log.e("CRASH", "DOUBT ID : $it.id")
                        }
                    }
                }
            }

        })
    }

    private fun showDialog() {
        val doubtDialog = AlertDialog.Builder(context!!).create()
        val doubtView = context!!.layoutInflater.inflate(R.layout.doubt_dialog, null)
        doubtView.cancelBtn.setOnClickListener {
            doubtDialog.dismiss()
        }
        doubtView.okBtn.setOnClickListener {
            if (doubtView.titleLayout.editText!!.text.length < 15 || doubtView.titleLayout.editText!!.text.isEmpty()) {
                doubtView.titleLayout.error = "Title length must be atleast 15 characters."
                return@setOnClickListener
            } else if (doubtView.descriptionLayout.editText!!.text.length < 20 || doubtView.descriptionLayout.editText!!.text.isEmpty()) {
                doubtView.descriptionLayout.error = "Description length must be atleast 20 characters."
                doubtView.titleLayout.error = ""
            } else {
                doubtView.descriptionLayout.error = ""
                val doubt = DoubtsJsonApi()
                doubt.body = doubtView.descriptionLayout.editText!!.text.toString()
                doubt.title = doubtView.titleLayout.editText!!.text.toString()
                doubt.category = 41
                val runAttempts = QuizRunAttempt() // type run-attempts
                val contents = Contents() // type contents
                runAttempts.id = param1
                contents.id = param2
                doubt.status = "DONE"
                doubt.postrunAttempt = runAttempts
                doubt.content = contents
                Clients.onlineV2JsonApi.createDoubt(doubt).enqueue(retrofitCallback { throwable, response ->
                    response?.body().let {
                        doubtDialog.dismiss()
                        thread {
                            doubtsDao.insert(DoubtsModel(it!!.id
                                    ?: "", it.title, it.body, it.content?.id
                                    ?: "", it.status, it.runAttempt?.id ?: ""
                            ))
                        }
                    }
                    info { throwable?.localizedMessage }
                })
            }
        }

        doubtDialog.window.setBackgroundDrawableResource(android.R.color.transparent)
        doubtDialog.setView(doubtView)
        doubtDialog.setCancelable(false)
        doubtDialog.show()
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                VideoDoubtFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
