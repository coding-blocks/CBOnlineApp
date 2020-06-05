package com.codingblocks.cbonlineapp.campaign

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RulesFragment : BaseCBFragment() {

    private val vm by sharedViewModel<CampaignViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_campaign_rules, container, false)
    }
}
