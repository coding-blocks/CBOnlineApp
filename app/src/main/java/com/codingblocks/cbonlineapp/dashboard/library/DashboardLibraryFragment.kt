package com.codingblocks.cbonlineapp.dashboard.library

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.commons.FragmentChangeListener
import kotlinx.android.synthetic.main.fragment_dashboard_library.*


class DashboardLibraryFragment : Fragment() {

    private lateinit var listener: FragmentChangeListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_dashboard_library, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dashboardLibraryEmptyBtn.setOnClickListener {
            listener.openExplore()
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as FragmentChangeListener
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
