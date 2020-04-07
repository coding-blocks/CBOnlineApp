package com.codingblocks.cbonlineapp.course

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.util.COURSE_ID
import com.codingblocks.cbonlineapp.util.COURSE_LOGO
import com.codingblocks.cbonlineapp.util.LOGO_TRANSITION_NAME
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.setRv
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import kotlinx.android.synthetic.main.activity_search_course.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchCourseActivity : BaseCBActivity() {

    private val viewModel by viewModel<CourseViewModel>()
    private val courseCardListAdapter = CourseListAdapter("LIST")
    val dialog by lazy {
        CourseSearchFragment()
    }
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

        viewModel.fetchAllCourses()
        courseSearchRv.setRv(
            this@SearchCourseActivity,
            courseCardListAdapter,
            orientation = RecyclerView.VERTICAL,
            setDivider = true
        )

        viewModel.suggestedCourses.observer(this) { courses ->
            courseCardListAdapter.submitList(courses)
        }

        searchBtn.setOnClickListener {
            dialog.show(supportFragmentManager, "course_search")
        }

        courseCardListAdapter.onItemClick = itemClickListener
    }
}
