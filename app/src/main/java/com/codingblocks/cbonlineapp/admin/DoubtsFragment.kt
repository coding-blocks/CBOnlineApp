package com.codingblocks.cbonlineapp.admin

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codingblocks.cbonlineapp.R


class DoubtsFragment : Fragment() {

    companion object {
        fun newInstance() = DoubtsFragment()
    }

    private lateinit var viewModel: DoubtsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.doubts_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DoubtsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
