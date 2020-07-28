package com.codingblocks.cbonlineapp.campaign

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AlertDialog
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.util.ShareUtils
import com.codingblocks.cbonlineapp.util.glide.GlideApp
import com.codingblocks.cbonlineapp.util.livedata.observer
import kotlinx.android.synthetic.main.dialog_result.view.*
import kotlinx.android.synthetic.main.dialog_share.view.fb
import kotlinx.android.synthetic.main.dialog_share.view.twitter
import kotlinx.android.synthetic.main.dialog_share.view.whatsapp
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
        GlideApp.with(requireContext())
            .load(R.drawable.wheel)
            .into(imageView)
        vm.spinsLiveData.observer(thisLifecycleOwner) {
            spinBtn.isEnabled = it > 0
            val with3digits = String.format("%02d", it)

            spinCountTv.text = with3digits
        }

        val runnable = Runnable {
            vm.fetchSpins()
            vm.fetchWinnings()
            try {

                vm.spinResponse?.let { res ->
                    imageView.animate()
                        .rotationBy(res.rotation.toFloat())
                        .setDuration(1000)
                        .setInterpolator(LinearInterpolator()).start()
                    showDialog(res.size, res.title, res.description)
                }
                spinBtn.isEnabled = true
            } catch (e: Exception) {
            }
        }

        spinBtn.setOnClickListener {
            vm.drawSpin()
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

    private fun showDialog(size: Int, prize: String, descriptionMsg: String) {
        val dialog = AlertDialog.Builder(requireContext()).create()
        val view = layoutInflater.inflate(R.layout.dialog_result, null)

        val title: String
        val subTitle: String
        val description: String
        val share: String
        val fbMsg: String
        val waMSg: String
        val twMsg: String
        val msg = "Signup using this link to get 500 credits in your wallet and stand a chance of winning amazing prizes this Summer using my referral code: https://cb.lk/join/${vm.referral}"
        val json: String
        if (size > 0) {
            title = "CONGRATULATIONS!"
            subTitle = "You won\n${prize.toUpperCase()}"
            description = descriptionMsg
            share = "Use hashtag #TurnYourLuck and show off your winnings on social media."
            fbMsg = "I am ecstatic to share that I have won $prize in Coding Blocks’s new Summer Learning Spree Campaign. Don’t wait any further. You can also win amazing prizes. Click on https://cb.lk/snwfb to participate in the Campaign and get an extra spin. #TurnYourLuck #CodingBlocks"
            waMSg = "I am so happy to share that I have won $prize in Coding Blocks ’ s new Summer Learning Spree Campaign.You can also win exciting prizes . Click on https://cb.lk/snwtw to participate in the Campaign and get an extra spin.# TurnYourLuck # CodingBlocks"
            twMsg = "I am ecstatic to share that I have won $prize in Coding Blocks ’ s new Summer Learning Spree Campaign.You can also win exciting prizes . Click on https://cb.lk/snwwa  to participate in the Campaign and get an extra spin.# TurnYourLuck # CodingBlocks @codingblocksin"
            json = "gift.json"
        } else {
            title = "OOPS!"
            subTitle = "Sorry \n Better Luck Next Time}"
            description = ""
            share = "For every friend who signup with your code you get an extra spin! https://cb.lk/join/${vm.referral}"
            fbMsg = msg
            waMSg = msg
            twMsg = msg
            json = "lose.json"
        }

        view.apply {
            resultAnimation.setAnimation(json)
            titleTv.text = title
            subtitleTv.text = subTitle
            descriptionTV.text = description
            shareTv.text = share

            fb.setOnClickListener {
                ShareUtils.shareToFacebook(fbMsg, requireContext())
                dialog.dismiss()
            }
            whatsapp.setOnClickListener {
                ShareUtils.shareToWhatsapp(waMSg, requireContext())
                dialog.dismiss()
            }
            twitter.setOnClickListener {
                ShareUtils.shareToTwitter(twMsg, requireContext())
                dialog.dismiss()
            }
        }
        dialog.apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setView(view)
            setCancelable(true)
            show()
        }
    }
}
