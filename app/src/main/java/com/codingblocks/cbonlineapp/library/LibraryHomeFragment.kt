package com.codingblocks.cbonlineapp.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import kotlinx.android.synthetic.main.fragment_course_library.*
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LibraryHomeFragment : BaseCBFragment(), View.OnClickListener {

    private val vm by sharedViewModel<LibraryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
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
                replaceFragmentSafely(
                    LibraryViewFragment(),
                    "LibraryView",
                    containerViewId = R.id.libraryContainer,
                    addToStack = true
                )
            }
            R.id.bookmarkBtn -> {
                vm.type = getString(R.string.bookmarks)
                replaceFragmentSafely(
                    LibraryViewFragment(),
                    "LibraryView",
                    containerViewId = R.id.libraryContainer,
                    addToStack = true
                )
            }
            R.id.downloadBtn -> {
                vm.type = getString(R.string.downloads)
                replaceFragmentSafely(
                    LibraryViewFragment(),
                    "LibraryView",
                    containerViewId = R.id.libraryContainer,
                    addToStack = true
                )
            }

            R.id.announcementsBtn -> {
                toast("Will be added Soon!")
                vm.type = getString(R.string.announcements)
            }
        }
    }
}
