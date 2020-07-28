package com.codingblocks.cbonlineapp.campaign

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
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
import com.codingblocks.cbonlineapp.dashboard.ViewPager2Adapter
import com.codingblocks.cbonlineapp.dashboard.ViewPager2Adapter.*
import com.codingblocks.cbonlineapp.dashboard.ViewPager2Adapter.FragmentName.*
import com.codingblocks.cbonlineapp.util.CustomDialog
import com.codingblocks.cbonlineapp.util.ShareUtils
import com.codingblocks.cbonlineapp.util.UNAUTHORIZED
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import com.codingblocks.cbonlineapp.util.livedata.observer
import com.codingblocks.onlineapi.ErrorStatus
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_my_course.*
import kotlinx.android.synthetic.main.activity_spin_win.*
import kotlinx.android.synthetic.main.dialog_share.view.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class CampaignActivity : BaseCBActivity() {

    val vm: CampaignViewModel by stateViewModel()
    private val pagerAdapter: ViewPager2Adapter by lazy { ViewPager2Adapter(this) }
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            toast(getString(R.string.logged_in))
        }
    }
    private val myClipboard: ClipboardManager by lazy {
        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spin_win)
        setToolbar(campaignToolbar)
        pagerAdapter.apply {
            add(CAMPAIGN_HOME)
            add(CAMPAIGN_WINNING)
            add(CAMPAIGN_LEADERBOARD)
            add(CAMPAIGN_LEADERBOARD)
        }
        campaignPager.apply {
            isUserInputEnabled = false
            adapter = pagerAdapter
            offscreenPageLimit = 2
        }

        TabLayoutMediator(myCampaignTabs, campaignPager) { tab, position ->
            tab.text = resources.getStringArray(R.array.campaign_tab_titles)[position]
            coursePager.setCurrentItem(tab.position, true)
        }.attach()
        vm.fetchSpins()
        vm.errorLiveData.observer(this) {
            when (it) {
                ErrorStatus.NO_CONNECTION -> {
                    showOffline()
                }
                ErrorStatus.UNAUTHORIZED -> {
                    CustomDialog.showConfirmation(this, UNAUTHORIZED) { result ->
                        if (result) {
                            startForResult(intentFor<LoginActivity>())
                        }
                    }
                }
            }
        }
        earnMore.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {
        val dialog = AlertDialog.Builder(this).create()
        val view = layoutInflater.inflate(R.layout.dialog_share, null)
        val msg = "Signup using this link to get 500 credits in your wallet and stand a chance of winning amazing prizes this Summer using my referral code: https://cb.lk/join/${vm.referral
            ?: ""}"
        view.apply {
            view.referralTv.append(vm.referral ?: "")
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
            copy_clipboard.setOnClickListener {
                val text = referralTv?.text
                val myClip = ClipData.newPlainText("referral", text)
                myClipboard.setPrimaryClip(myClip)
                toast("Copied to clipboard")
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
