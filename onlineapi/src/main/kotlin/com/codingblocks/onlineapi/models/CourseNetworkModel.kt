package com.codingblocks.onlineapi.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.Type

/**
 * Data class for Course model
 * https://bitbucket.org/coding-blocks/amoeba-backend/src/a997903143f08d80af22e791204dbe2f1c800d3d/src/models/Course.js#lines-10
 */
@Type("courses", "course")
data class CourseNetworkModel(
    val title: String = "",
    val subtitle: String = "",
    val logo: String = "",
    val summary: String = "",
    val categoryId: Int,
    val promoVideo: String = "",
    val reviewCount: Int = 0,
    val difficulty: String = "",
    val rating: Float = 0f,
    val slug: String = "",
    val coverImage: String = "",
    val faq: String,
    @JsonProperty("coursefeatures")
    val courseFeatures: ArrayList<CourseFeatureNetworkModel>?,
    @Relationship("instructors")
    val instructors: ArrayList<InstructorNetworkModel>?,
    @Relationship("runs")
    val runs: ArrayList<RunNetworkModel>?,
    @Relationship("active-runs", "active_runs")
    val activeRuns: List<RunNetworkModel>?,
    @Relationship("projects")
    var projects: ArrayList<Project>?,
    @Relationship("tags")
    val tags: ArrayList<Tags>?
) : BaseNetworkModel() {

    constructor(id: String) :
        this(
            "", "", "", "", 0, "", 0, "", 0f, "",
            "", "", null, null, null, null, null, null
        ) {
        super.id = id
    }

    /** function to get [Runs] for enrolling into Trial */
    fun getTrialRun(tier: String): RunNetworkModel? {
        return with(activeRuns ?: runs!!) {
            groupBy { it.tier }[tier]?.firstOrNull()
                ?: minByOrNull { it.price }
        }
    }

    /** function to get [Runs] to display in [Sections] */
    fun getContentRun(tier: String): RunNetworkModel? {
        return with(activeRuns ?: runs!!) {
            getTrialRun(tier)
                ?: groupBy { it.tier }["LIVE"]?.firstOrNull()
        }
    }
}
