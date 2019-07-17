package com.codingblocks.cbonlineapp.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import kotlinx.android.synthetic.main.fragment_annoucements.view.joinWhatsAppGroupView
import kotlinx.android.synthetic.main.fragment_annoucements.view.joinWhatsAppButton
import kotlinx.android.synthetic.main.fragment_annoucements.view.instructorRv
import org.koin.androidx.viewmodel.ext.android.viewModel

class AnnouncementsFragment : Fragment() {
    lateinit var courseId: String
    lateinit var whatsAppLink: String
    lateinit var attemptId: String
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private val viewModel by viewModel<AnnouncementsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            courseId = it.getString(ARG_COURSE_ID) ?: ""
            attemptId = it.getString(RUN_ATTEMPT_ID) ?: ""
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

        viewModel.getRunByAtemptId(attemptId).observer(this) {
            whatsAppLink = it.whatsappLink
            if (!it.premium || whatsAppLink.isEmpty()) {
                view.joinWhatsAppGroupView.visibility = View.GONE
            } else {
                view.joinWhatsAppGroupView.visibility = View.VISIBLE
            }
        }

        view.joinWhatsAppButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setPackage("com.whatsapp")
            intent.data = Uri.parse(whatsAppLink)
            if (this@AnnouncementsFragment.requireContext().packageManager.resolveActivity(intent, 0) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this@AnnouncementsFragment.requireContext(), "Please install whatsApp", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, attemptId: String) =
            AnnouncementsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_COURSE_ID, param1)
                    putString(RUN_ATTEMPT_ID, attemptId)
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
