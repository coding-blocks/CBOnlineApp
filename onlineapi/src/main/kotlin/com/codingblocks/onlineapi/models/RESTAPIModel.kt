package com.codingblocks.onlineapi.models

import com.google.gson.annotations.SerializedName

data class Doubts(

        @field:SerializedName("primary_groups")
        val primaryGroups: List<Any?>? = null,

        @field:SerializedName("topic_list")
        val topicList: TopicList? = null
)

data class TopicList(

        @field:SerializedName("can_create_topic")
        val canCreateTopic: Boolean? = null,

        @field:SerializedName("per_page")
        val perPage: Int? = null,

        @field:SerializedName("topics")
        val topics: ArrayList<TopicsItem?>? = null
)

data class TopicsItem(

        @field:SerializedName("created_at")
        val createdAt: String? = null,

        @field:SerializedName("title")
        val title: String? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("last_poster_username")
        val lastPosterUsername: String? = null
)

data class PostersItem(

        @field:SerializedName("primary_group_id")
        val primaryGroupId: Any? = null,

        @field:SerializedName("user_id")
        val userId: Int? = null,

        @field:SerializedName("extras")
        val extras: Any? = null,

        @field:SerializedName("description")
        val description: String? = null
)

data class RatingModel(
        val rating: String,
        val count: Int,
        val stats: List<Double>,
        val userScore: Any?
)

data class Leaderboard(@JvmField
                  var userName: String) : BaseModel() {

    @JvmField
    var collegeName: String? = null
    @JvmField
    var photo: String? = null
    @JvmField
    var score: Int? = 0
}
data class PostStream(
        val unpinned: Any? = null,
        val hasDeleted: Boolean? = null,
        val pinned: Boolean? = null,
        val chunkSize: Int? = null,
        val suggestedTopics: List<SuggestedTopicsItem?>? = null,
        val currentPostNumber: Int? = null,
        val featuredLink: Any? = null,
        val createdAt: String? = null,
        val deletedBy: Any? = null,
        val topicTimer: Any? = null,
        val title: String? = null,
        val postStream: PostStream? = null,
        val timelineLookup: List<List<Int?>?>? = null,
        val archived: Boolean? = null,
        val hasSummary: Boolean? = null,
        val privateTopicTimer: Any? = null,
        val wordCount: Any? = null,
        val fancyTitle: String? = null,
        val categoryId: Int? = null,
        val actionsSummary: List<ActionsSummaryItem?>? = null,
        val pinnedAt: String? = null,
        val draft: Any? = null,
        val draftSequence: Int? = null,
        val draftKey: String? = null,
        val details: Details? = null,
        val id: Int? = null,
        val views: Int? = null,
        val slug: String? = null,
        val lastPostedAt: Any? = null,
        val likeCount: Int? = null,
        val visible: Boolean? = null,
        val messageBusLastId: Int? = null,
        val pendingPostsCount: Int? = null,
        val bookmarked: Any? = null,
        val pinnedGlobally: Boolean? = null,
        val replyCount: Int? = null,
        val deletedAt: Any? = null,
        val tags: List<Any?>? = null,
        val archetype: String? = null,
        val participantCount: Int? = null,
        val userId: Int? = null,
        val pinnedUntil: Any? = null,
        val pmWithNonHumanUser: Boolean? = null,
        val highestPostNumber: Int? = null,
        val closed: Boolean? = null,
        val postsCount: Int? = null,
        val stream: List<Int?>? = null,
        val posts: List<PostsItem?>? = null
)

data class PostsItem(
        val hidden: Boolean? = null,
        val canWiki: Boolean? = null,
        val moderator: Boolean? = null,
        val wiki: Boolean? = null,
        val createdAt: String? = null,
        val admin: Boolean? = null,
        val trustLevel: Int? = null,
        val score: Double? = null,
        val canViewEditHistory: Boolean? = null,
        val updatedAt: String? = null,
        val actionsSummary: List<ActionsSummaryItem?>? = null,
        val incomingLinkCount: Int? = null,
        val canDelete: Boolean? = null,
        val primaryGroupFlairBgColor: Any? = null,
        val postType: Int? = null,
        val id: Int? = null,
        val topicId: Int? = null,
        val quoteCount: Int? = null,
        val avatarTemplate: String? = null,
        val primaryGroupFlairColor: Any? = null,
        val read: Boolean? = null,
        val editReason: Any? = null,
        val primaryGroupFlairUrl: Any? = null,
        val hiddenReasonId: Any? = null,
        val cooked: String? = null,
        val reads: Int? = null,
        val canEdit: Boolean? = null,
        val staff: Boolean? = null,
        val replyCount: Int? = null,
        val replyToPostNumber: Any? = null,
        val version: Int? = null,
        val deletedAt: Any? = null,
        val userId: Int? = null,
        val primaryGroupName: Any? = null,
        val canRecover: Any? = null,
        val name: String? = null,
        val userTitle: Any? = null,
        val userDeleted: Boolean? = null,
        val postNumber: Int? = null,
        val yours: Boolean? = null,
        val topicSlug: String? = null,
        val username: String? = null,
        val avgTime: Int? = null,
        val displayUsername: String? = null
)

data class User(
        val id: Int? = null,
        val avatarTemplate: String? = null,
        val username: String? = null
)

data class SuggestedTopicsItem(
        val unpinned: Any? = null,
        val pinned: Boolean? = null,
        val unread: Int? = null,
        val featuredLink: Any? = null,
        val createdAt: String? = null,
        val newPosts: Int? = null,
        val bumped: Boolean? = null,
        val title: String? = null,
        val lastReadPostNumber: Int? = null,
        val liked: Boolean? = null,
        val archived: Boolean? = null,
        val fancyTitle: String? = null,
        val categoryId: Int? = null,
        val id: Int? = null,
        val bumpedAt: String? = null,
        val slug: String? = null,
        val views: Int? = null,
        val lastPostedAt: String? = null,
        val visible: Boolean? = null,
        val likeCount: Int? = null,
        val imageUrl: Any? = null,
        val bookmarked: Boolean? = null,
        val posters: List<PostersItem?>? = null,
        val replyCount: Int? = null,
        val tags: List<Any?>? = null,
        val archetype: String? = null,
        val highestPostNumber: Int? = null,
        val closed: Boolean? = null,
        val notificationLevel: Int? = null,
        val unseen: Boolean? = null,
        val postsCount: Int? = null
)

data class ParticipantsItem(
        val primaryGroupFlairColor: Any? = null,
        val primaryGroupName: Any? = null,
        val primaryGroupFlairUrl: Any? = null,
        val primaryGroupFlairBgColor: Any? = null,
        val id: Int? = null,
        val postCount: Int? = null,
        val avatarTemplate: String? = null,
        val username: String? = null
)

data class LastPoster(
        val id: Int? = null,
        val avatarTemplate: String? = null,
        val username: String? = null
)

data class CreatedBy(
        val id: Int? = null,
        val avatarTemplate: String? = null,
        val username: String? = null
)

data class ActionsSummaryItem(
        val hidden: Boolean? = null,
        val count: Int? = null,
        val canAct: Boolean? = null,
        val id: Int? = null
)
data class Details(
        val lastPoster: LastPoster? = null,
        val canReplyAsNewTopic: Boolean? = null,
        val canInviteTo: Boolean? = null,
        val canRemoveSelfId: Int? = null,
        val canEdit: Boolean? = null,
        val notificationLevel: Int? = null,
        val canCreatePost: Boolean? = null,
        val createdBy: CreatedBy? = null,
        val canFlagTopic: Boolean? = null,
        val canMovePosts: Boolean? = null,
        val canRemoveAllowedUsers: Boolean? = null,
        val participants: List<ParticipantsItem?>? = null
)

