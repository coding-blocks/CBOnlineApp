package com.codingblocks.onlineapi.models

data class SpinResponse(
    val img: String,
    val chance: Int,
    val rotation: Int,
    val count: Int,
    val description: String,
    val webp: String,
    val title: String,
    val createdAt: String,
    val size: Int,
    val theme: String,
    val id: String,
    val actions: List<String>,
    val updatedAt: String
)
