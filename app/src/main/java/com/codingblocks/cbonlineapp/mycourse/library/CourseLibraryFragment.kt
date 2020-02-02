package com.codingblocks.cbonlineapp.mycourse.library

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.library.LibraryActivity
import com.codingblocks.cbonlineapp.mycourse.MyCourseViewModel
import com.codingblocks.cbonlineapp.util.COURSE_NAME
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.TYPE
import kotlinx.android.synthetic.main.fragment_course_library.*
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CourseLibraryFragment : BaseCBFragment(), View.OnClickListener {

    private val viewModel by sharedViewModel<MyCourseViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
        View? = inflater.inflate(R.layout.fragment_course_library, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notesBtn.setOnClickListener(this)
        downloadBtn.setOnClickListener(this)
        bookmarkBtn.setOnClickListener(this)
        announcementsBtn.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val intent = Intent(requireContext(), LibraryActivity::class.java)
        intent.putExtra(COURSE_NAME, viewModel.name)
        intent.putExtra(RUN_ATTEMPT_ID, viewModel.attemptId)
        when (v.id) {
            R.id.notesBtn -> {
                intent.putExtra(TYPE, getString(R.string.notes))
                startActivity(intent)
            }
            R.id.bookmarkBtn -> {
                intent.putExtra(TYPE, getString(R.string.bookmarks))
                startActivity(intent)
            }
            R.id.downloadBtn -> {
                intent.putExtra(TYPE, getString(R.string.downloads))
                startActivity(intent)
            }

            R.id.announcementsBtn -> {
                toast("Will be added Soon!")
                intent.putExtra(TYPE, getString(R.string.announcements))
            }
        }
    }
}
