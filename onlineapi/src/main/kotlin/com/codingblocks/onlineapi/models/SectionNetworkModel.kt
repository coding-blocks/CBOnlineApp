package com.codingblocks.onlineapi.models

import com.github.jasminb.jsonapi.Links
import com.github.jasminb.jsonapi.RelType
import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.RelationshipLinks
import com.github.jasminb.jsonapi.annotations.Type

@Type("sections")
data class SectionNetworkModel(
    var name: String? = null,
    var premium: Boolean = false,
    var status: String? = null,
    var order: Int? = 0,
    @Relationship("contents", relType = RelType.RELATED)
    var contents: ArrayList<LectureContent>? = null,
    val runId: String? = "",
    @RelationshipLinks("contents")
    val courseContentLinks: Links? = null
) : BaseNetworkModel() {
    constructor(id: String) : this() {
        super.id = id
    }
}
