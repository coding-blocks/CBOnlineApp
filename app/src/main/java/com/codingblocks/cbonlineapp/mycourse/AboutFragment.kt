package com.codingblocks.cbonlineapp.mycourse

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.database.models.InstructorModel
import com.codingblocks.cbonlineapp.insturctors.InstructorDataAdapter
import com.codingblocks.cbonlineapp.util.ARG_COURSE_ID
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.extensions.getPrefs
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.fragment_annoucements.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AboutFragment : Fragment() {
    lateinit var courseId: String
    var whatsAppLink: String? = null
    lateinit var attemptId: String
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    var firstTime: Boolean = false

    private val viewModel by sharedViewModel<MyCourseViewModel>()

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

        return inflater.inflate(R.layout.fragment_annoucements, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        joinWhatsAppButton.setOnClickListener {
            if (whatsAppLink.isNullOrEmpty()) {
                Toast.makeText(this@AboutFragment.requireContext(), "Whatsapp Group Not Available for Trial Courses", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setPackage("com.whatsapp")
                intent.data = Uri.parse(whatsAppLink)
                if (this@AboutFragment.requireContext().packageManager.resolveActivity(intent, 0) != null) {
                    startActivity(intent)
                } else {
                    Toast.makeText(this@AboutFragment.requireContext(), "Please install whatsApp", Toast.LENGTH_SHORT).show()
                }
            }
        }
        val instructorList = ArrayList<InstructorModel>()
        val instructorAdapter = InstructorDataAdapter(instructorList)
        instructorRv.layoutManager = LinearLayoutManager(context)
        instructorRv.adapter = instructorAdapter
        firstTime = true
        viewModel.getInstructor().observer(this) {
            instructorAdapter.setData(it as ArrayList<InstructorModel>)
        }

//        viewModel.getRun().observer(this) {
//            whatsAppLink = it.whatsappLink
//            joinWhatsAppGroupView.isVisible = whatsAppLink?.run { true } ?: false
//        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, attemptId: String) =
            AboutFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_COURSE_ID, param1)
                    putString(RUN_ATTEMPT_ID, attemptId)
                }
            }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && !firstTime) {
            if (view != null) {
                val params = Bundle()
                params.putString(FirebaseAnalytics.Param.ITEM_ID, getPrefs()?.SP_ONEAUTH_ID)
                params.putString(FirebaseAnalytics.Param.ITEM_NAME, "CourseAnnouncement")
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params)
            }
        }
    }
}
