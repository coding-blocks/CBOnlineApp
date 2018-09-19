package com.codingblocks.cbonlineapp

import com.google.gson.annotations.SerializedName

data class User(val id: Int, val username: String, val firstname: String, val lastname: String, val photo: String, val email: String, val createdAt: String, val updatedAt: String)


data class CourseModel(
        val meta: Meta,
        val data: List<DataModel>,
        val included: List<Included>
)

data class Meta(
        val pagination: Pagination
)

data class Pagination(
        val count: Int,
        val currentOffset: Int,
        val nextOffset: Int,
        val prevOffset: Int
)

data class DataModel(
        val type: String,
        val id: String,
        val attributes: AttributesModel,
        val relationships: RelationshipsModel
)

data class AttributesModel(
        val title: String,
        val subtitle: String,
        val logo: String,
        @SerializedName("category-name")
        val categoryName: String,
        @SerializedName("category-id")
        val categoryId: Int,
        @SerializedName("doubt-sub-category-id")
        val doubtSubCategoryId: Int,
        val summary: String,
        @SerializedName("promo-video")
        val promoVideo: String,
        val difficulty: Int,
        val rating: Float,
        val slug: String,
        val unlisted: Boolean,
        @SerializedName("review-count")
        val reviewCount: Int,
        @SerializedName("created-by")
        val createdBy: Any?,
        @SerializedName("reviewed-by")
        val reviewedBy: Any?,
        @SerializedName("published-by")
        val publishedBy: Any?,
        @SerializedName("cover-image")
        val coverImage: String
)

data class RelationshipsModel(
        val instructors: Instructors,
        val runs: Runs
)

data class Runs(
        val data: List<Data>
)

data class Data(
        val type: String,
        val id: String
)

data class Instructors(
        val data: List<Data>
)

data class Included(
        val type: String,
        val id: String,
        val attributes: Attributes,
        val relationships: Relationships
)

data class Relationships(
        val courses: Courses
)

data class Courses(
        val data: List<Data>
)

data class Attributes(
        val id: String,
        val name: String,
        val description: String,
        val photo: String
)