package com.codingblocks.cbonlineapp.campaign

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.util.livedata.observer
import io.noties.markwon.Markwon
import kotlinx.android.synthetic.main.fragment_campaign_rules.*
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val markWon = Markwon.create(requireContext())
        vm.fetchRules().observer(thisLifecycleOwner) {
            val text = it.getString("Rules")!!.replace("_b", "\n", true)
            markWon.setMarkdown(rulesTv, text)
        }
    }
}
