package com.codingblocks.cbonlineapp.admin.doubts

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.commons.FragmentChangeListener
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.cbonlineapp.util.UNAUTHORIZED
import com.codingblocks.cbonlineapp.util.extensions.changeViewState
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.openChrome
import com.codingblocks.cbonlineapp.util.extensions.setRv
import com.codingblocks.cbonlineapp.util.extensions.showEmptyView
import com.codingblocks.cbonlineapp.util.extensions.showShimmer
import com.codingblocks.onlineapi.ErrorStatus
import com.codingblocks.onlineapi.models.Doubts
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_admin.*
import kotlinx.android.synthetic.main.doubts_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class AdminDoubtsFragment : BaseCBFragment(), TabLayout.OnTabSelectedListener {

    private val sharedPrefs by inject<PreferenceHelper>()

    private lateinit var listener: FragmentChangeListener

    private val doubtsAdapter = DoubtsAdapter()
    private val viewModel by viewModel<AdminDoubtsViewModel>()

    private val ackClickListener: AckClickListener by lazy {
        object : AckClickListener {
            override fun onClick(doubtId: String, doubt: Doubts) {
                viewModel.acknowledgeDoubt(doubtId, doubt)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as FragmentChangeListener
    }

    private val resolveClickListener: ResolveClickListener by lazy {
        object : ResolveClickListener {
            override fun onClick(doubtId: String, doubt: Doubts) {
                sharedPrefs.SP_USER_ID.let { viewModel.acknowledgeDoubt(doubtId, doubt, it) }
            }
        }
    }

    private val discussClickListener: DiscussClickListener by lazy {
        object : DiscussClickListener {
            override fun onClick(discordId: String) {
                requireContext().openChrome("https://discuss.codingblocks.com/t/$discordId")
            }
        }
    }

    private val chatClickListener: ChatClickListener by lazy {
        object : ChatClickListener {
            override fun onClick(convId: String, doubtId: String) {
                if (convId.isEmpty()) {
                    GlobalScope.launch(Dispatchers.Main) {
                        val id = viewModel.requestChat(doubtId)
                        if (id.isNotEmpty()) {
                            startChat(id)
                        }
                    }
                } else {
                    startChat(convId)
                }
            }
        }
    }

    private fun startChat(id: String) {
        listener.openInbox(id)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.doubts_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.fetchLiveDoubts()
        doubtShimmer.startShimmer()
//        setupWorker()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adminTabLayout.addOnTabSelectedListener(this)

        adminDoubtRv.apply {
            adminDoubtRv.isVisible = true
            setRv(requireContext(), doubtsAdapter)
        }

        viewModel.listDoubtsResponse.observer(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                if (adminTabLayout.selectedTabPosition == 0) {
                    emptyMessageTv.text = getString(R.string.empty_live_doubt)
                } else {
                    emptyMessageTv.text = getString(R.string.empty_my_doubt)
                }
            } else {
                doubtsAdapter.submitList(it)
            }
            changeViewState(adminDoubtRv, internetll, emptyll, doubtShimmer, it.isEmpty())
        }

        viewModel.errorLiveData.observer(viewLifecycleOwner) {
            when (it) {
                ErrorStatus.NO_CONNECTION -> {
                    showEmptyView(internetll, emptyll, doubtShimmer)
                }
                ErrorStatus.UNAUTHORIZED -> {
                    Components.showConfirmation(requireContext(), UNAUTHORIZED) {
                        requireActivity().finish()
                    }
                }
                ErrorStatus.TIMEOUT -> {
//                    root.showSnackbar(it, Snackbar.LENGTH_INDEFINITE, bottomNavAdmin) {
//                        fetchDoubts(adminTabLayout.selectedTabPosition)
//                    }
                }
            }
        }

        viewModel.barMessage.observer(viewLifecycleOwner) {
//            root.showSnackbar(it, Snackbar.LENGTH_INDEFINITE, bottomNavAdmin, false)
        }

        doubtsAdapter.apply {
            onAckClick = ackClickListener
            onChatClick = chatClickListener
            onDiscussClick = discussClickListener
            onResolveClick = resolveClickListener
        }
        viewModel.nextOffSet.observer(viewLifecycleOwner) { offSet ->
            nextBtn.isEnabled = offSet != -1
            nextBtn.setOnClickListener {
                viewModel.fetchLiveDoubts(offSet)
            }
        }

        viewModel.prevOffSet.observer(viewLifecycleOwner) { offSet ->
            prevBtn.isEnabled = offSet != -1
            prevBtn.setOnClickListener {
                viewModel.fetchLiveDoubts(offSet)
            }
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        // Clear Doubts While Changing the tab
        fetchDoubts(tab.position)
    }

    private fun fetchDoubts(position: Int) {
        doubtsAdapter.clear()
        showShimmer(internetll, emptyll, doubtShimmer)
        when (position) {
            0 -> {
                viewModel.fetchLiveDoubts()
            }
            1 -> {
                sharedPrefs.SP_USER_ID.let { viewModel.fetchMyDoubts(it) }
            }
        }
    }

    override fun onDestroyView() {
        doubtsAdapter.apply {
            onAckClick = null
            onResolveClick = null
            onChatClick = null
            onDiscussClick = null
        }
        super.onDestroyView()
    }
}
