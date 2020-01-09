package com.codingblocks.onlineapi.models

data class PerformanceResponse(
    val performance: Performance,
    val averageProgress: List<AverageProgressItem>,
    val userProgress: List<UserProgressItem>
)

data class Performance(
    val percentile: Int,
    val remarks: String
)

data class AverageProgressItem(
    val date: String,
    val progress: String
)

data class UserProgressItem(
    val date: String,
    val progress: String
)
