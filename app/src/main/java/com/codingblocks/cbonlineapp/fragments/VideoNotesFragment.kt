package com.codingblocks.cbonlineapp.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.retrofitCallback
import com.codingblocks.cbonlineapp.adapters.VideosNotesAdapter
import com.codingblocks.cbonlineapp.database.AppDatabase
import com.codingblocks.cbonlineapp.database.models.NotesModel
import com.codingblocks.cbonlineapp.util.OnItemClickListener
import com.codingblocks.cbonlineapp.extensions.observeOnce
import com.codingblocks.cbonlineapp.extensions.observer
import com.codingblocks.onlineapi.Clients
import kotlinx.android.synthetic.main.fragment_notes.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info


private const val ARG_ATTEMPT_ID = "param1"

class VideoNotesFragment : Fragment(), AnkoLogger {
    private var param1: String? = null

    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(context!!)
    }

    private val notesDao by lazy {
        database.notesDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_ATTEMPT_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notes, container, false)

        fetchNotes()
        val notesList = ArrayList<NotesModel>()
        val notesAdapter = VideosNotesAdapter(notesList, object : OnItemClickListener {
            override fun onItemClick(position: Int, id: String) {
                try {
                    (activity as OnItemClickListener).onItemClick(position, id)
                } catch (cce: ClassCastException) {

                }
            }

        })
        view.notesRv.layoutManager = LinearLayoutManager(context)
        view.notesRv.adapter = notesAdapter
        val itemDecorator = DividerItemDecoration(context!!, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(ContextCompat.getDrawable(context!!, R.drawable.divider)!!)
        view.notesRv.addItemDecoration(itemDecorator)


        notesDao.getNotes(param1!!).observer(this) {
            notesAdapter.setData(it as ArrayList<NotesModel>)
            if (it.isEmpty()) {
                view.notesRv.visibility = View.GONE
                view.emptyTv.visibility = View.VISIBLE
            } else {
                view.notesRv.visibility = View.VISIBLE
                view.emptyTv.visibility = View.GONE
            }
        }


        return view
    }

    private fun fetchNotes() {
        val networkList: ArrayList<NotesModel> = ArrayList()
        Clients.onlineV2JsonApi.getNotesByAttemptId(param1!!).enqueue(retrofitCallback { throwable, response ->
            response?.body().let { notesList ->
                if (response?.isSuccessful == true) {
                    notesList?.forEach {
                        try {
                            networkList.add(
                                NotesModel(
                                    it.id
                                        ?: "",
                                    it.duration ?: 0.0,
                                    it.text ?: "",
                                    it.content?.id
                                        ?: "",
                                    it.runAttempt?.id ?: "",
                                    it.createdAt ?: "",
                                    it.deletedAt
                                        ?: ""
                                )
                            )
                        } catch (e: Exception) {
                            info { "error" + e.localizedMessage }
                        }
                    }
                    if (networkList.size == notesList?.size) {
                        notesDao.insertAll(networkList)
                        notesDao.getNotes(param1!!).observeOnce { list ->
                            // remove items which are deleted
                            val sum = list + networkList
                            sum.groupBy { it.nttUid }
                                    .filter { it.value.size == 1 }
                                    .flatMap { it.value }
                                    .forEach {
                                        notesDao.deleteNoteByID(it.nttUid)
                                    }
                        }
                    }
                }
            }

        })
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
                VideoNotesFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_ATTEMPT_ID, param1)
                    }
                }
    }
}
