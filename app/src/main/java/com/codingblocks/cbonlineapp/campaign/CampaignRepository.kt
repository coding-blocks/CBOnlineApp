package com.codingblocks.cbonlineapp.campaign

import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.safeApiCall
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CampaignRepository(private val firestore: FirebaseFirestore) {

    suspend fun getSpinStats() = safeApiCall { Clients.api.spinStats() }
    suspend fun drawSpin() = safeApiCall { Clients.api.drawSpin() }
    suspend fun getMyWinnings() = safeApiCall { Clients.onlineV2JsonApi.getWinnings() }
    suspend fun getReferral() = safeApiCall { Clients.api.myReferral() }

    suspend fun getRules(): DocumentSnapshot = firestore.collection("Campaign").document("spinnwin").get().await()


}
