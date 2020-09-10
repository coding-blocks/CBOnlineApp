package com.codingblocks.onlineapi.models

import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.Type

@Type("comments", "comment")
data class CommentNetworkModel(
    val body: String = "",
    val username: String = "",
    val discourseTopicId: String = "",
    @Relationship("doubt")
    val doubt: DoubtNetworkModel? = null
) : BaseNetworkModel()