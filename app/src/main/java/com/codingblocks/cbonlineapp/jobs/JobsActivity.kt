package com.codingblocks.cbonlineapp.jobs

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.widgets.SheetDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import com.google.android.material.tabs.TabLayout
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_jobs.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class JobsActivity : AppCompatActivity() {

    private val viewModel by viewModel<JobsViewModel>()
    private val jobsAdapter = JobsAdapter(JobsDiffCallback())

    private val bottomSheetDialog by lazy {
        SheetDialog(this, R.style.sheetStyle)
    }

    private var locationList = listOf<String>()
    private var jobtypeList = listOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jobs)

        rvJobs.layoutManager = LinearLayoutManager(this)
        rvJobs.adapter = jobsAdapter

        viewModel.getJobs()

        viewModel.getAllJobs().observer(this) { jobs ->
            viewModel.allJobList.clear()
            viewModel.allJobList.addAll(jobs)
            jobsAdapter.submitList(viewModel.allJobList)
            locationList = jobs.map { it.location }.distinct()
            jobtypeList = jobs.map { it.type }.distinct()
            setupBottomFilterSheet()
        }

        viewModel.filteredJobsProgress.observer(this) {
            if (it) {
                jobsAdapter.notifyDataSetChanged()
            }
        }

        filter_button.setOnClickListener {
            bottomSheetDialog.show()
        }
    }

    private fun setupBottomFilterSheet() {
        @SuppressLint("InflateParams") val sheetView = layoutInflater.inflate(R.layout.sheet_filter, null)

        setupTabFilters(sheetView)
        setupFilterChips(sheetView)
        val buttonApply = sheetView.findViewById<AppCompatButton>(R.id.btnApply)
        buttonApply.setOnClickListener {
            applyFilters()
            bottomSheetDialog.cancel()
        }

        bottomSheetDialog.setContentView(sheetView)

        val bottomSheetBehavior = BottomSheetBehavior.from(sheetView.parent as View)
        bottomSheetBehavior.bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {
            }

            override fun onStateChanged(p0: View, p1: Int) {
                if (p1 == BottomSheetBehavior.STATE_HIDDEN) {
                    applyFilters()
                    bottomSheetDialog.cancel()
                }
            }
        }
    }

    private fun applyFilters() {
        viewModel.getFilteredList()
    }

    private fun setupFilterChips(sheetView: View) {
        val locationGroup = sheetView.findViewById<ChipGroup>(R.id.locationChipGroup)
        val jobtypeGroup = sheetView.findViewById<ChipGroup>(R.id.jobTypeChipGroup)

        locationGroup.addView(Chip(this).apply {
            val drawable = ChipDrawable.createFromAttributes(context, null, 0, R.style.Widget_MaterialComponents_Chip_Filter)
            setChipDrawable(drawable)
            text = "All"
            setOnCheckedChangeListener { _, b ->
                locationGroup.children.forEach { chip ->
                    if (chip is Chip && chip.text != "All") {
                        chip.isChecked = b
                    }
                }
            }
        })
        locationList.forEach { item ->
            locationGroup.addView(Chip(this).apply {
                val drawable = ChipDrawable.createFromAttributes(context, null, 0, R.style.Widget_MaterialComponents_Chip_Filter)
                setChipDrawable(drawable)
                text = item
                setOnCheckedChangeListener { _, b ->
                    viewModel.searchFilters.filterLocation.apply {
                        if (b)
                            add(item)
                        else
                            remove(item)
                        distinct()
                    }
                }
            })
        }

        jobtypeGroup.addView(Chip(this).apply {
            val drawable = ChipDrawable.createFromAttributes(context, null, 0, R.style.Widget_MaterialComponents_Chip_Filter)
            setChipDrawable(drawable)
            text = "All"
            setOnCheckedChangeListener { _, b ->
                jobtypeGroup.children.forEach { chip ->
                    if (chip is Chip && chip.text != "All") {
                        chip.isChecked = b
                    }
                }
            }
        })
        jobtypeList.forEach { item ->
            jobtypeGroup.addView(Chip(this).apply {
                val drawable = ChipDrawable.createFromAttributes(context, null, 0, R.style.Widget_MaterialComponents_Chip_Filter)
                setChipDrawable(drawable)
                text = item
                setOnCheckedChangeListener { _, b ->
                    viewModel.searchFilters.filterJobtype.apply {
                        if (b)
                            add(item)
                        else
                            remove(item)
                        distinct()
                    }
                }
            })
        }
    }

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

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }
}
