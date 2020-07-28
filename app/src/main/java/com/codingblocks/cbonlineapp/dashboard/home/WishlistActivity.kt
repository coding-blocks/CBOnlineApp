package com.codingblocks.cbonlineapp.dashboard.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.course.CourseActivity
import com.codingblocks.cbonlineapp.util.COURSE_ID
import com.codingblocks.cbonlineapp.util.COURSE_LOGO
import com.codingblocks.cbonlineapp.util.LOGO_TRANSITION_NAME
import com.codingblocks.cbonlineapp.util.livedata.observer
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_wishlist.*
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class WishlistActivity : AppCompatActivity() {

    val wishlistViewModel: WishlistViewModel by stateViewModel()
    private val wishlistAdapter = WishlistPagedAdapter("LIST")

    private val itemClickListener: WishListItemClickListener by lazy {
        object : WishListItemClickListener {
            override fun onClick(id: String, name: String, logo: CircleImageView) {
                val intent = Intent(this@WishlistActivity, CourseActivity::class.java)
                intent.putExtra(COURSE_ID, id)
                intent.putExtra(COURSE_LOGO, name)
                intent.putExtra(LOGO_TRANSITION_NAME, ViewCompat.getTransitionName(logo))

                val options: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@WishlistActivity,
                        logo,
                        ViewCompat.getTransitionName(logo)!!
                    )
                startActivity(intent, options.toBundle())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wishlist)
        wishlistRv.apply {
            adapter = wishlistAdapter
            layoutManager = LinearLayoutManager(applicationContext)
        }
        wishlistAdapter.onItemClick = itemClickListener
        wishlistViewModel.fetchWishList().observer(this) { wishlist ->
            wishlistShimmerLayout.isVisible = false
            wishlistAdapter.submitList(wishlist)
        }
    }
}
