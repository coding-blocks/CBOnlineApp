package com.codingblocks.cbonlineapp.campaign

import androidx.lifecycle.SavedStateHandle
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel

class CampaignViewModel(
    handle: SavedStateHandle,
    private val repo: CampaignRepository
) : BaseCBViewModel() {

}
