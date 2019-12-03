package com.codingblocks.cbonlineapp.admin.doubts

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.admin.FragmentChangeListener
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.UNAUTHORIZED
import com.codingblocks.cbonlineapp.util.extensions.changeViewState
import com.codingblocks.cbonlineapp.util.extensions.getPrefs
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.showEmptyView
import com.codingblocks.cbonlineapp.util.extensions.showShimmer
import com.codingblocks.onlineapi.ErrorStatus
import com.codingblocks.onlineapi.models.Doubts
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_admin.*
import kotlinx.android.synthetic.main.doubts_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.AnkoLogger
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class DoubtsFragment : Fragment(), AnkoLogger, TabLayout.OnTabSelectedListener {

    val userId by lazy {
        getPrefs()?.SP_USER_ID
    }

    private lateinit var listener: FragmentChangeListener

    private val doubtsAdapter = DoubtsAdapter()
    private val viewModel by viewModel<DoubtsViewModel>()

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
                userId?.let { viewModel.acknowledgeDoubt(doubtId, doubt, it) }
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
        setupWorker()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adminTabLayout.addOnTabSelectedListener(this)

        doubtRv.apply {
            doubtRv.isVisible = true
            layoutManager = LinearLayoutManager(requireContext())
            adapter = doubtsAdapter
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
            changeViewState(doubtRv, internetll, emptyll, doubtShimmer, it.isEmpty())
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
                    Snackbar.make(root, it, Snackbar.LENGTH_SHORT)
                        .setAnchorView(bottomNavAdmin)
                        .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                        .setAction("Retry") {
                            fetchDoubts(adminTabLayout.selectedTabPosition)
                        }
                        .show()
                }
            }
        }

        viewModel.barMessage.observer(viewLifecycleOwner) {
            Snackbar.make(root, it, Snackbar.LENGTH_SHORT)
                .setAnchorView(bottomNavAdmin)
                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                .show()
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
                userId?.let { viewModel.fetchMyDoubts(it) }
            }
        }
    }

    private fun setupWorker() {
        val constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val request = PeriodicWorkRequestBuilder<DoubtWorker>(PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance()
            .enqueue(request)
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
