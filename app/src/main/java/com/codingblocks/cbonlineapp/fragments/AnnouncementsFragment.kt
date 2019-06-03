package com.codingblocks.cbonlineapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.adapters.InstructorDataAdapter
import com.codingblocks.cbonlineapp.database.models.Instructor
import com.codingblocks.cbonlineapp.extensions.getPrefs
import com.codingblocks.cbonlineapp.extensions.observer
import com.codingblocks.cbonlineapp.viewmodels.AnnouncementsViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.codingblocks.cbonlineapp.util.ARG_COURSE_ID
import kotlinx.android.synthetic.main.fragment_annoucements.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class AnnouncementsFragment : Fragment() {
    lateinit var courseId: String
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private val viewModel by viewModel<AnnouncementsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            courseId = it.getString(ARG_COURSE_ID) ?: ""
        }
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_annoucements, container, false)

        val instructorList = ArrayList<Instructor>()
        val instructorAdapter = InstructorDataAdapter(instructorList)
        view.instructorRv.layoutManager = LinearLayoutManager(context)
        view.instructorRv.adapter = instructorAdapter

        viewModel.getInstructorWithCourseId(courseId).observer(this) {
            instructorAdapter.setData(it as ArrayList<Instructor>)
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            AnnouncementsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_COURSE_ID, param1)
                }
            }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if (view != null) {
                val params = Bundle()
                params.putString(FirebaseAnalytics.Param.ITEM_ID, getPrefs()?.SP_ONEAUTH_ID)
                params.putString(FirebaseAnalytics.Param.ITEM_NAME, "CourseAnnouncement")
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params)
            }
        }
    }
}
