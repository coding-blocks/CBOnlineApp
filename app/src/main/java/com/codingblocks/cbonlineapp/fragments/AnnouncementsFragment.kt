package com.codingblocks.cbonlineapp.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.adapters.InstructorDataAdapter
import com.codingblocks.cbonlineapp.database.AppDatabase
import com.codingblocks.cbonlineapp.database.Instructor
import kotlinx.android.synthetic.main.fragment_annoucements.view.*

private const val ARG__COURSE_ID = "course_id"

class AnnouncementsFragment : Fragment() {
    private lateinit var database: AppDatabase
    lateinit var courseId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            courseId = it.getString(ARG__COURSE_ID)!!
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_annoucements, container, false)
        database = AppDatabase.getInstance(context!!)
        val instructorDao = database.courseWithInstructorDao()
        val instructorList = ArrayList<Instructor>()
        val instructorAdapter = InstructorDataAdapter(instructorList)
        view.instructorRv.layoutManager = LinearLayoutManager(context)
        view.instructorRv.adapter = instructorAdapter

        instructorDao.getInstructorWithCourseId(courseId).observe(this, Observer<List<Instructor>> {
            instructorAdapter.setData(it as ArrayList<Instructor>)
        })

        return view
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String) =
                AnnouncementsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG__COURSE_ID, param1)
                    }
                }
    }


}
