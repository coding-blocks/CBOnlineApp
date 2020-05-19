package com.codingblocks.onlineapi.models

data class PerformanceResponse(
    val performance: Performance?,
    val averageProgress: ArrayList<ProgressItem>,
    val userProgress: ArrayList<ProgressItem>
)

data class Performance(
    val percentile: Int?,
    val remarks: String
)

data class ProgressItem(
    val date: String,
    val progress: String
)

data class RankResponse(
    val bestRank: Int,
    val bestRankAchievedOn: String,
    val currentMonthScore: Int,
    val currentOverallRank: Int,
    val previousMonthScore: Int,
    val previousOverallRank: Int
)

data class Comparision(val name: String, val lite: Boolean, val premium: Boolean, val live: Boolean, val classroom: Boolean)


