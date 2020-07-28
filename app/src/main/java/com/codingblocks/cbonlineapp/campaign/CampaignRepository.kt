package com.codingblocks.cbonlineapp.campaign

import com.codingblocks.onlineapi.CBOnlineLib
import com.codingblocks.onlineapi.safeApiCall
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CampaignRepository(private val firestore: FirebaseFirestore) {

    suspend fun getSpinStats() = safeApiCall { CBOnlineLib.api.spinStats() }
    suspend fun drawSpin() = safeApiCall { CBOnlineLib.api.drawSpin() }
    suspend fun getMyWinnings() = safeApiCall { CBOnlineLib.onlineV2JsonApi.getWinnings() }
    suspend fun getReferral() = safeApiCall { CBOnlineLib.api.myReferral() }

    suspend fun getRules(): DocumentSnapshot = firestore.collection("Campaign").document("spinnwin").get().await()
}
