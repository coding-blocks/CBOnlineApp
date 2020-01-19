package com.codingblocks.cbonlineapp.library

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import kotlinx.android.synthetic.main.fragment_course_library.*
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LibraryHomeFragment : Fragment(), View.OnClickListener {

    private val vm by sharedViewModel<LibraryViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
        View? = inflater.inflate(R.layout.fragment_course_library, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.fetchSections()
        notesBtn.setOnClickListener(this)
        downloadBtn.setOnClickListener(this)
        bookmarkBtn.setOnClickListener(this)
        announcementsBtn.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.notesBtn -> {
                vm.type = getString(R.string.notes)
                replaceFragmentSafely(LibraryViewFragment(), "LibraryView", containerViewId = R.id.libraryContainer, enterAnimation = R.animator.slide_in_right, exitAnimation = R.animator.slide_out_left, addToStack = true)
            }
            R.id.bookmarkBtn -> {
                vm.type = getString(R.string.bookmarks)
                replaceFragmentSafely(LibraryViewFragment(), "LibraryView", containerViewId = R.id.libraryContainer, enterAnimation = R.animator.slide_in_right, exitAnimation = R.animator.slide_out_left, addToStack = true)
            }
            R.id.downloadBtn -> {
                toast("Will be added Soon!")
                vm.type = getString(R.string.downloads)
            }

            R.id.announcementsBtn -> {
                toast("Will be added Soon!")
                vm.type = getString(R.string.announcements)
            }
        }
    }
}
