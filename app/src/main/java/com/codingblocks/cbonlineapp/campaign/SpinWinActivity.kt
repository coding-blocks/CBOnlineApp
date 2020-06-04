package com.codingblocks.cbonlineapp.campaign

import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.R
import kotlinx.android.synthetic.main.activity_spin_win.*
import org.koin.androidx.viewmodel.ext.android.stateViewModel


class SpinWinActivity : AppCompatActivity() {

    val vm:CampaignViewModel by stateViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spin_win)


        val runnable = Runnable {

            imageView.animate().cancel()
            spinBtn.isEnabled = true
        }

        spinBtn.setOnClickListener {
            imageView.rotation = 0f
            spinBtn.isEnabled = false
            imageView.animate()
                .rotationBy((360 * 10).toFloat())
                .withEndAction(runnable)
                .setDuration(6000)
                .setInterpolator(LinearInterpolator()).start()

            imageView.clearAnimation()
        }
    }
}
