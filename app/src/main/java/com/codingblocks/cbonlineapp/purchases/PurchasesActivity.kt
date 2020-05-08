package com.codingblocks.cbonlineapp.purchases

import android.os.Bundle
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.dashboard.DashboardViewModel
import com.codingblocks.cbonlineapp.dashboard.mycourses.ItemClickListener
import com.codingblocks.cbonlineapp.dashboard.mycourses.MyCourseListAdapter
import com.codingblocks.cbonlineapp.mycourse.MyCourseActivity
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.setRv
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import kotlinx.android.synthetic.main.activity_purchases.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PurchasesActivity : BaseCBActivity() {

    private val viewModel by viewModel<DashboardViewModel>()
    private val courseListAdapter = MyCourseListAdapter()

    private val itemClickListener: ItemClickListener by lazy {
        object : ItemClickListener {
            override fun onClick(id: String, runId: String, runAttemptId: String, name: String) {
                startActivity(MyCourseActivity.createMyCourseActivityIntent(
                    this@PurchasesActivity,
                    runAttemptId,
                    name
                ))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchases)
        setToolbar(purchasesToolbar)
        purchasedCoursesRv.setRv(this, courseListAdapter, true)
        viewModel.purchasedRuns.observer(this) {
            courseListAdapter.submitList(it)
        }
        courseListAdapter.onItemClick = itemClickListener
    }
}
