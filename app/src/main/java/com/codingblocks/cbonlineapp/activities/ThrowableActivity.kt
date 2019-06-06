package com.codingblocks.cbonlineapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.codingblocks.cbonlineapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_throwable.backBtn
import kotlinx.android.synthetic.main.activity_throwable.team1Flag
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop

class ThrowableActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_throwable)

        backBtn.setOnClickListener {
            startActivity(intentFor<HomeActivity>().singleTop())
        }

    }
}
