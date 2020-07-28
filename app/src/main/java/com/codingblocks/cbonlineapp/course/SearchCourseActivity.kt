package com.codingblocks.cbonlineapp.course

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.course.adapter.ItemClickListener
import com.codingblocks.cbonlineapp.course.adapter.PagedCourseListAdapter
import com.codingblocks.cbonlineapp.util.COURSE_ID
import com.codingblocks.cbonlineapp.util.COURSE_LOGO
import com.codingblocks.cbonlineapp.util.LOGO_TRANSITION_NAME
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import com.codingblocks.cbonlineapp.util.recyclerciew.DividerItemDecorator
import kotlinx.android.synthetic.main.activity_search_course.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchCourseActivity : BaseCBActivity() {

    private val viewModel: CourseViewModel by viewModel()
    private val courseCardListAdapter = PagedCourseListAdapter("LIST")
    val dialog: CourseSearchFragment by lazy { CourseSearchFragment() }
    private val itemClickListener: ItemClickListener by lazy {
        object : ItemClickListener {
            override fun onClick(id: String, name: String, logo: ImageView) {
                val intent = Intent(this@SearchCourseActivity, CourseActivity::class.java)
                intent.putExtra(COURSE_ID, id)
                intent.putExtra(COURSE_LOGO, name)
                intent.putExtra(LOGO_TRANSITION_NAME, ViewCompat.getTransitionName(logo))

                val options: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@SearchCourseActivity,
                        logo,
                        ViewCompat.getTransitionName(logo)!!
                    )
                startActivity(intent, options.toBundle())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_course)
        setToolbar(searchToolbar, title = "All Courses")
        courseSearchRv.apply {
            layoutManager = LinearLayoutManager(this@SearchCourseActivity, RecyclerView.VERTICAL, false)
            addItemDecoration(DividerItemDecorator(ContextCompat.getDrawable(this@SearchCourseActivity, R.drawable.divider)!!))
            adapter = courseCardListAdapter
        }

        viewModel.getCourses().observe(
            this,
            Observer { courses ->
                courseCardListAdapter.submitList(courses)
            }
        )

        searchBtn.setOnClickListener {
            dialog.show(supportFragmentManager, "course_search")
        }

        courseCardListAdapter.onItemClick = itemClickListener
    }
}
