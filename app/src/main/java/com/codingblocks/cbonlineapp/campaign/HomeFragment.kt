package com.codingblocks.cbonlineapp.campaign

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.util.extensions.observer
import kotlinx.android.synthetic.main.fragment_campaign_home.*
import org.jetbrains.anko.AnkoLogger
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class HomeFragment : BaseCBFragment(), AnkoLogger {

    private val vm: CampaignViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_campaign_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.spinsLiveData.observer(viewLifecycleOwner) {
            spinBtn.isEnabled = it > 0
            val with3digits = String.format("%02d", it)

            spinCountTv.text = with3digits
        }

        val runnable = Runnable {
            imageView.animate().cancel()
            spinBtn.isEnabled = true
        }

        spinBtn.setOnClickListener {
            vm.drawSpin().observer(viewLifecycleOwner) {

            }
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
