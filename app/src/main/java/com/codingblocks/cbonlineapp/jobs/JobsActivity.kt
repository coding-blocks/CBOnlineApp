package com.codingblocks.cbonlineapp.jobs

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.util.ALL
import com.codingblocks.cbonlineapp.util.APPLIED
import com.codingblocks.cbonlineapp.util.ELIGIBLE
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.setRv
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import com.codingblocks.cbonlineapp.util.widgets.SheetDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.ChipGroup
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_jobs.*
import org.jetbrains.anko.design.indefiniteSnackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class JobsActivity : BaseCBActivity() {

    private val viewModel by viewModel<JobsViewModel>()
    private val jobsAdapter = JobsAdapter()

    private val bottomSheetDialog: SheetDialog by lazy {
        SheetDialog(this, R.style.sheetStyle)
    }

    private var locationList = listOf<String>()
    private var jobtypeList = listOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jobs)
        setToolbar(jobsToolbar)
        setUpFilterChips()
        rvJobs.setRv(this, jobsAdapter, true, "thick")

        viewModel.getJobs().observer(this) {
            jobsAdapter.submitList(it)
        }

//        viewModel.getAllJobs().observer(this) { jobs ->
//            viewModel.allJobList.clear()
//            viewModel.allJobList.addAll(jobs)
//            updateJobsList()
//            locationList = jobs.map { it.location }.distinct()
//            jobtypeList = jobs.map { it.type }.distinct()
//            setupBottomFilterSheet()
//        }

//        viewModel.filteredJobsProgress.observer(this) {
//            if (it) {
//                shimmerJobs.isVisible = true
//                rvJobs.isVisible = false
//                tvNoJobs.isVisible = false
//            } else {
//                shimmerJobs.isVisible = false
//                rvJobs.isVisible = !viewModel.noFilteredJobs
//                tvNoJobs.isVisible = viewModel.noFilteredJobs
//                updateJobsList()
//            }
//        }

//        viewModel.jobProgress.observe(this) {
//            if (it) {
//                shimmerJobs.isVisible = true
//                rvJobs.isVisible = false
//            } else {
//                shimmerJobs.isVisible = false
//                rvJobs.isVisible = true
//            }
//        }

        viewModel.errorLiveData.observer(this) {
            shimmerJobs.isVisible = false
            rootJobs.indefiniteSnackbar(it, "Retry") {
                viewModel.getJobs()
            }
        }

//        filter_button.setOnClickListener {
//            bottomSheetDialog.show()
//        }
    }

    private fun setUpFilterChips() {
        liveDoubtBtn.setOnClickListener {
            viewModel.type.value = ALL
        }

        resolvedDoubtBtn.setOnClickListener {
            viewModel.type.value = ELIGIBLE
        }

        allDoubtBtn.setOnClickListener {
            viewModel.type.value = APPLIED
        }
        viewModel.type.observer(this) {
            when (it) {
                ALL -> {
                    liveDoubtBtn.isActivated = true
                    resolvedDoubtBtn.isActivated = false
                    allDoubtBtn.isActivated = false
                }
                ELIGIBLE -> {
                    liveDoubtBtn.isActivated = false
                    resolvedDoubtBtn.isActivated = true
                    allDoubtBtn.isActivated = false
                }
                APPLIED -> {
                    liveDoubtBtn.isActivated = false
                    resolvedDoubtBtn.isActivated = false
                    allDoubtBtn.isActivated = true
                }
                else -> {
                    liveDoubtBtn.isActivated = false
                    resolvedDoubtBtn.isActivated = false
                    allDoubtBtn.isActivated = false
                }
            }
        }
    }

//    private fun updateJobsList() {
//        val updatedJobs = mutableListOf<JobsModel>().apply {
//            addAll(viewModel.allJobList)
//        }
//        jobsAdapter.submitList(updatedJobs)
//    }

    private fun setupBottomFilterSheet() {
        val sheetView = layoutInflater.inflate(R.layout.sheet_filter, null)

        setupTabFilters(sheetView)
//        setupFilterChips(sheetView)
        val buttonApply = sheetView.findViewById<AppCompatButton>(R.id.btnApply)
        buttonApply.setOnClickListener {
            applyFilters()
            bottomSheetDialog.hide()
        }

        bottomSheetDialog.setContentView(sheetView)

        val bottomSheetBehavior = BottomSheetBehavior.from(sheetView.parent as View)
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {
            }

            override fun onStateChanged(p0: View, p1: Int) {
                if (p1 == BottomSheetBehavior.STATE_HIDDEN) {
                    applyFilters()
                    bottomSheetDialog.cancel()
                }
            }
        })
    }

    private fun applyFilters() {
        viewModel.getFilteredList()
    }

//    private fun setupFilterChips(sheetView: View) {
//        val locationGroup = sheetView.findViewById<ChipGroup>(R.id.locationChipGroup)
//        val jobtypeGroup = sheetView.findViewById<ChipGroup>(R.id.jobTypeChipGroup)
//
//        locationGroup.addView(Chip(this).apply {
//            val drawable = ChipDrawable.createFromAttributes(context, null, 0, R.style.Widget_MaterialComponents_Chip_Filter)
//            setChipDrawable(drawable)
//            text = "All"
//            setOnCheckedChangeListener { _, b ->
//                locationGroup.children.forEach { chip ->
//                    if (chip is Chip && chip.text != "All") {
//                        chip.isChecked = b
//                    }
//                }
//            }
//        })
//        locationList.forEach { item ->
//            locationGroup.addView(Chip(this).apply {
//                val drawable = ChipDrawable.createFromAttributes(context, null, 0, R.style.Widget_MaterialComponents_Chip_Filter)
//                setChipDrawable(drawable)
//                text = item
//                setOnCheckedChangeListener { _, b ->
//                    viewModel.searchFilters.filterLocation.apply {
//                        if (b)
//                            add(item)
//                        else
//                            remove(item)
//                        distinct()
//                    }
//                }
//            })
//        }
//
//        jobtypeGroup.addView(Chip(this).apply {
//            val drawable = ChipDrawable.createFromAttributes(context, null, 0, R.style.Widget_MaterialComponents_Chip_Filter)
//            setChipDrawable(drawable)
//            text = "All"
//            setOnCheckedChangeListener { _, b ->
//                jobtypeGroup.children.forEach { chip ->
//                    if (chip is Chip && chip.text != "All") {
//                        chip.isChecked = b
//                    }
//                }
//            }
//        })
//        jobtypeList.forEach { item ->
//            jobtypeGroup.addView(Chip(this).apply {
//                val drawable = ChipDrawable.createFromAttributes(context, null, 0, R.style.Widget_MaterialComponents_Chip_Filter)
//                setChipDrawable(drawable)
//                text = item
//                setOnCheckedChangeListener { _, b ->
//                    viewModel.searchFilters.filterJobtype.apply {
//                        if (b)
//                            add(item)
//                        else
//                            remove(item)
//                        distinct()
//                    }
//                }
//            })
//        }
//    }

    private fun setupTabFilters(sheetView: View) {
        val tabLayout = sheetView.findViewById<TabLayout>(R.id.tabLayout)
        val tabLocation = tabLayout.newTab().setText(getString(R.string.filter_location))
        val tabJobType = tabLayout.newTab().setText(getString(R.string.filter_job_type))
        val locationGroup = sheetView.findViewById<ChipGroup>(R.id.locationChipGroup)
        val jobtypeGroup = sheetView.findViewById<ChipGroup>(R.id.jobTypeChipGroup)

        tabLayout.addTab(tabLocation)
        tabLayout.addTab(tabJobType)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.text) {
                    getString(R.string.filter_location) -> {
                        locationGroup.visibility = View.VISIBLE
                        jobtypeGroup.visibility = View.INVISIBLE
                    }
                    getString(R.string.filter_job_type) -> {
                        locationGroup.visibility = View.INVISIBLE
                        jobtypeGroup.visibility = View.VISIBLE
                    }
                }
            }
        })
    }
}
