package com.codingblocks.onlineapi.models

import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.Type

@Type("contents")
data class ContentNetworkModel(
    val contentable: String,
    val duration: Long,
    val title: String,
    val sectionContent: SectionContent?,
    @Relationship("code_challenge", "code-challenge")
    val codeChallenge: ContentCodeChallenge?,
    @Relationship("document")
    val document: ContentDocumentType?,
    @Relationship("lecture")
    val lecture: ContentLectureType?,
    @Relationship("progress")
    val progress: ContentProgress?,
    @Relationship("video")
    val video: ContentVideoType?,
    @Relationship("qna")
    val qna: ContentQna?,
    @Relationship("csv")
    val csv: ContentCsv?,
    @Relationship("bookmark")
    val bookmark: Bookmark?
) : BaseNetworkModel() {
    constructor(id: String) :
        this("", 0L, "", null, null, null, null, null, null, null, null, null) {
        super.id = id
    }
}
