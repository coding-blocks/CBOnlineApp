package com.codingblocks.onlineapi.models

import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.Type

@Type("runs", "run")
data class RunNetworkModel(
    val name: String = "",
    val description: String = "",
    val start: String = "",
    val end: String = "",
    val price: String = "",
    val mrp: String? = "",
    val unlisted: Boolean,
    val enrollmentStart: String = "",
    val enrollmentEnd: String = "",
    val whatsappLink: String?,
    val productId: Int?,
    val completionThreshold: Int?,
    val goodiesThreshold: Int?,
    val totalContents: Int,
    val tier: String?,
    @Relationship("sections")
    val sections: ArrayList<Sections>?,
    @Relationship("run-attempts", "run_attempts")
    var runAttempts: ArrayList<RunAttemptNetworkModel>?,
    @Relationship("course")
    var course: Course?,
    @Relationship("ratings")
    var rating: ArrayList<Rating>?
) : BaseNetworkModel()
