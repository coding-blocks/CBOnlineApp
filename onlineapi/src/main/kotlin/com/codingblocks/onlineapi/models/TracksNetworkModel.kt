package com.codingblocks.onlineapi.models

import com.github.jasminb.jsonapi.Links
import com.github.jasminb.jsonapi.RelType
import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.RelationshipLinks
import com.github.jasminb.jsonapi.annotations.Type

@Type("career_tracks")
data class TracksNetworkModel(
    var name: String = "",
    var slug: String = "",
    var description: String? = "",
    var unlisted: Boolean,
    var logo: String = "",
    var background: String = "",
    var status: String? = "",
    val languages: List<String>,
    @Relationship("courses", relType = RelType.RELATED)
    var courses: List<CourseNetworkModel>?,
    @Relationship("professions")
    var professions: List<Professions>?,
    @RelationshipLinks("courses")
    val coursesLinks: Links? = null
) : BaseNetworkModel()
