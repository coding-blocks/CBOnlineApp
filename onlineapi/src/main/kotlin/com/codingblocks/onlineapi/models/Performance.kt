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

data class Comparision(val name: String, val lite: Boolean, val premium: Boolean, val live: Boolean, val classroom: Boolean)


