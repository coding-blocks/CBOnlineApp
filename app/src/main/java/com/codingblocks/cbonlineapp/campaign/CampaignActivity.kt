package com.codingblocks.cbonlineapp.campaign

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.invoke
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.auth.LoginActivity
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.commons.TabLayoutAdapter
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.ShareUtils
import com.codingblocks.cbonlineapp.util.UNAUTHORIZED
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import com.codingblocks.onlineapi.ErrorStatus
import kotlinx.android.synthetic.main.activity_spin_win.*
import kotlinx.android.synthetic.main.dialog_share.view.*
import org.jetbrains.anko.intentFor
import org.koin.androidx.viewmodel.ext.android.stateViewModel


class CampaignActivity : BaseCBActivity() {

    val vm: CampaignViewModel by stateViewModel()
    private val pagerAdapter by lazy { TabLayoutAdapter(supportFragmentManager) }
    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            // Handle the Intent

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spin_win)
        setToolbar(campaignToolbar)
        myCampaignTabs.setupWithViewPager(campaignPager)
        pagerAdapter.apply {
            add(HomeFragment(), getString(R.string.spin_wheel))
            add(WinningsFragment(), getString(R.string.winnings))
            add(LeaderBoardFragment(), getString(R.string.leaderboard))
            add(RulesFragment(), getString(R.string.rules))
        }
        vm.fetchSpins()
        vm.errorLiveData.observer(this) {
            when (it) {
                ErrorStatus.NO_CONNECTION -> {
                    showOffline()
                }
                ErrorStatus.UNAUTHORIZED -> {
                    Components.showConfirmation(this, UNAUTHORIZED) {
                        if (it) {
                            startForResult(intentFor<LoginActivity>())
                        } else {
                            finish()
                        }
                    }
                }
            }
        }
        campaignPager.apply {
            setPagingEnabled(true)
            adapter = pagerAdapter
            currentItem = 0
            offscreenPageLimit = 2
        }


        earnMore.setOnClickListener {
            showDialog()
        }

    }

    private fun showDialog() {
        val dialog = AlertDialog.Builder(this).create()
        val view = layoutInflater.inflate(R.layout.dialog_share, null)
        val msg = "Signup using this link to get 500 credits in your wallet and stand a chance of winning amazing prizes this Summer using my referral code: https://cb.lk/join/${vm.referral?:""}"
        view.apply {
            view.referralTv.append(vm.referral?:"")
            fb.setOnClickListener {
                ShareUtils.shareToFacebook(msg, this@CampaignActivity)
                dialog.dismiss()

            }
            whatsapp.setOnClickListener {
                ShareUtils.shareToWhatsapp(msg, this@CampaignActivity)
                dialog.dismiss()

            }
            twitter.setOnClickListener {
                ShareUtils.shareToTwitter(msg, this@CampaignActivity)
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

    companion object {

        fun createCampaignActivityIntent(context: Context): Intent {
            return context.intentFor<CampaignActivity>()
        }
    }
}
