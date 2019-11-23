package com.codingblocks.cbonlineapp.admin.doubts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.onlineapi.ErrorStatus
import org.jetbrains.anko.AnkoLogger
import org.koin.androidx.viewmodel.ext.android.viewModel


class DoubtsFragment : Fragment(), AnkoLogger {

    companion object {
        fun newInstance() = DoubtsFragment()
    }

    private val viewModel by viewModel<DoubtsViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.doubts_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.listDoubtsResponse.observer(viewLifecycleOwner) {

        }

        viewModel.errorLiveData.observer(viewLifecycleOwner)
        {
            when (it) {
                ErrorStatus.EMPTY_RESPONSE -> {

                }
                ErrorStatus.NO_CONNECTION -> {

                }
                ErrorStatus.UNAUTHORIZED -> {

                }
                ErrorStatus.TIMEOUT -> {

                }
            }
        }


    }

}
