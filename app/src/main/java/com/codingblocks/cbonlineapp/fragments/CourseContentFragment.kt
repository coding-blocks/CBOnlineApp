package com.codingblocks.cbonlineapp.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.adapters.SectionDetailsAdapter
import com.codingblocks.cbonlineapp.database.AppDatabase
import com.codingblocks.cbonlineapp.database.CourseSection
import kotlinx.android.synthetic.main.activity_course.view.*
import kotlinx.android.synthetic.main.fragment_course_content.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class CourseContentFragment : Fragment(), AnkoLogger {

    private lateinit var database: AppDatabase
    lateinit var attempt_Id: String


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_course_content, container, false)
        attempt_Id = arguments?.getString(ARG__ATTEMPT_ID)!!
        database = AppDatabase.getInstance(context!!)
        val sectionDao = database.setionDao()
        val sectionsList = ArrayList<CourseSection>()
        val sectionAdapter = SectionDetailsAdapter(sectionsList, activity!!)
        view.rvExpendableView.layoutManager = LinearLayoutManager(context)
        view.rvExpendableView.adapter = sectionAdapter


        sectionDao.getCourseSection(attempt_Id).observe(this, Observer<List<CourseSection>> {
            info {
                "sections$it"
                sectionAdapter.setData(it as ArrayList<CourseSection>)
            }
        })

        return view
    }

    companion object {

        private const val ARG__ATTEMPT_ID = "attempt_id"

        fun newInstance(attemot_id: String): CourseContentFragment {
            val fragment = CourseContentFragment()
            val args = Bundle()
            args.putString(ARG__ATTEMPT_ID, attemot_id)
            fragment.arguments = args
            return fragment
        }
    }


}
