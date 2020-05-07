package com.codingblocks.onlineapi.models

import com.google.gson.annotations.SerializedName


data class DoubtStats(
    val avgFirstResponse: Float,
    val avgRating: Float,
    val avgResolution: Float,
    val cbRating: Float,
    val lastFeedback: Any?,
    val totalBadReviews: Int,
    val totalResolvedDoubts: Int
)

data class Thumbnail(
    val posters: List<PostersItem>
)

data class PostersItem(
    val url: String,
    val height: Int
)

data class RatingModel(
    val rating: String,
    val count: Int,
    val stats: List<Double>,
    val userScore: Any?
)

data class Leaderboard(
    @JvmField
    var userName: String) : BaseModel() {
    @JvmField
    var collegeName: String? = null
    @JvmField
    var photo: String? = null
    @JvmField
    var score: Int? = 0
}



data class ResetRunAttempt(
    val runAttemptId: String
)

data class Extension(
    val description: String,
    @field:SerializedName("created_at")
    val createdAt: String,
    val type: String,
    @field:SerializedName("display_slug")
    val displaySlug: String,
    @field:SerializedName("product_category_id")
    val productCategoryId: Int,
    @field:SerializedName("is_offline")
    val isOffline: Boolean,
    @field:SerializedName("duration")
    val duration: Any? = null,
    @field:SerializedName("updated_at")
    val updatedAt: String? = null,
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("product_extensions")
    val productExtensions: List<ProductExtensionsItem>,

    @field:SerializedName("owner_user_id")
    val ownerUserId: Int? = null,

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("mrp")
    val mrp: Int? = null,

    @field:SerializedName("list_price")
    val listPrice: Int? = null,

    @field:SerializedName("extension_of")
    val extensionOf: Any? = null,

    @field:SerializedName("deleted_at")
    val deletedAt: Any? = null,

    @field:SerializedName("tax_id")
    val taxId: Int? = null,

    @field:SerializedName("listed")
    val listed: Boolean? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("updated_by")
    val updatedBy: Any? = null,

    @field:SerializedName("owner_client_id")
    val ownerClientId: Int? = null,

    @field:SerializedName("emi_min_base")
    val emiMinBase: Int? = null,

    @field:SerializedName("per_user")
    val perUser: Int? = null,

    @field:SerializedName("redirect_url")
    val redirectUrl: String? = null,

    @field:SerializedName("product_category")
    val productCategory: ProductCategory? = null,

    @field:SerializedName("emi_min_repeat")
    val emiMinRepeat: Int? = null
)

data class ProductCategory(

    @field:SerializedName("emi_allowed")
    val emiAllowed: Boolean? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("sac_code")
    val sacCode: Any? = null,

    @field:SerializedName("hsn_code")
    val hsnCode: String? = null,

    @field:SerializedName("deleted_at")
    val deletedAt: Any? = null
)

data class ProductExtensionsItem(

    @field:SerializedName("instances")
    val instances: Int? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("type")
    val type: String? = null,

    @field:SerializedName("display_slug")
    val displaySlug: String? = null,

    @field:SerializedName("product_category_id")
    val productCategoryId: Int? = null,

    @field:SerializedName("is_offline")
    val isOffline: Boolean? = null,

    @field:SerializedName("duration")
    val duration: Int? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("owner_user_id")
    val ownerUserId: Int? = null,

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("mrp")
    val mrp: Int? = null,

    @field:SerializedName("list_price")
    val listPrice: Int? = null,

    @field:SerializedName("extension_of")
    val extensionOf: String? = null,

    @field:SerializedName("deleted_at")
    val deletedAt: Any? = null,

    @field:SerializedName("tax_id")
    val taxId: Int? = null,

    @field:SerializedName("listed")
    val listed: Boolean? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("updated_by")
    val updatedBy: Int? = null,

    @field:SerializedName("owner_client_id")
    val ownerClientId: Int? = null,

    @field:SerializedName("emi_min_base")
    val emiMinBase: Int? = null,

    @field:SerializedName("per_user")
    val perUser: Int? = null,

    @field:SerializedName("redirect_url")
    val redirectUrl: String? = null,

    @field:SerializedName("emi_min_repeat")
    val emiMinRepeat: Int? = null
)


