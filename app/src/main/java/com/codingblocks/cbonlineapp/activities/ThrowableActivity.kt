package com.codingblocks.cbonlineapp.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.R
import kotlinx.android.synthetic.main.activity_throwable.backBtn
import kotlinx.android.synthetic.main.activity_throwable.infoll
import kotlinx.android.synthetic.main.activity_throwable.quiz1ll
import kotlinx.android.synthetic.main.activity_throwable.submitBtn

class ThrowableActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_throwable)

        backBtn.setOnClickListener {
            finish()
        }


        submitBtn.setOnClickListener {
            infoll.animate()
                .translationY(infoll.height.toFloat())
                .alpha(0.0f)
                .setDuration(300)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        infoll.visibility = View.GONE

                        quiz1ll.animate()
                            .alpha(1.0f)
                            .setDuration(200)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator?) {
                                    quiz1ll.visibility = View.VISIBLE
                                }

                            })
                    }

                })
        }
    }


    override fun onStart() {
        super.onStart()


    }
}
