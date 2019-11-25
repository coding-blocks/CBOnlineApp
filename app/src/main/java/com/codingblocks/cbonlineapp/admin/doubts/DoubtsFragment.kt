package com.codingblocks.cbonlineapp.admin.doubts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.onlineapi.ErrorStatus
import com.codingblocks.onlineapi.models.Doubts
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.doubts_fragment.*
import org.jetbrains.anko.AnkoLogger
import org.koin.androidx.viewmodel.ext.android.viewModel


class DoubtsFragment : Fragment(), AnkoLogger, TabLayout.OnTabSelectedListener {

    companion object {
        fun newInstance() = DoubtsFragment()
    }

    private val doubtsAdapter = DoubtsAdapter()
    private val viewModel by viewModel<DoubtsViewModel>()


    private val ackClickListener: AckClickListener by lazy {
        object : AckClickListener {
            override fun onClick(doubtId: String, doubt: Doubts) {
                viewModel.acknowledgeDoubt(doubtId, doubt)
            }
        }
    }

    private val discussClickListener: DiscussClickListener by lazy {
        object : DiscussClickListener {
            override fun onClick(discordId: String) {
                Components.openChrome(requireContext(), "https://discuss.codingblocks.com/t/$discordId")
            }
        }
    }

    private val chatClickListener: ChatClickListener by lazy {
        object : ChatClickListener {
            override fun onClick(convId: String) {
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.doubts_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.fetchLiveDoubts()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adminTabLayout.addOnTabSelectedListener(this)

        doubtRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = doubtsAdapter
        }

        viewModel.listDoubtsResponse.observer(viewLifecycleOwner) {
            doubtsAdapter.submitList(it)
        }

        viewModel.errorLiveData.observer(viewLifecycleOwner)
        {
            when (it) {
                ErrorStatus.EMPTY_RESPONSE -> {
                    doubtsAdapter.clear()
                }
                ErrorStatus.NO_CONNECTION -> {

                }
                ErrorStatus.UNAUTHORIZED -> {

                }
                ErrorStatus.TIMEOUT -> {

                }
            }
        }

        doubtsAdapter.apply {
            onAckClick = ackClickListener
            onChatClick = chatClickListener
            onDiscussClick = discussClickListener
        }

    }

    override fun onTabReselected(tab: TabLayout.Tab) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        when (tab.position) {
            0 -> {
            }
            1 -> viewModel.fetchLiveDoubts()
            2 -> {

                viewModel.fetchMyDoubts("238594")
            }
        }
    }

    override fun onDestroyView() {
        doubtsAdapter.apply {
            onAckClick = null
        }
        super.onDestroyView()
    }

}
