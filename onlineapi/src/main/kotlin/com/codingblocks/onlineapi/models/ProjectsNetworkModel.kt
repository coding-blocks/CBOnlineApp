package com.codingblocks.onlineapi.models

import com.github.jasminb.jsonapi.annotations.Type

/**
 * Data class for Project model
 * https://bitbucket.org/coding-blocks/amoeba-backend/src/master/src/models/Project.js
 */
@Type("projects")
data class ProjectsNetworkModel(
    val title: String = "",
    val description: String = "",
    val image: String = ""
) : BaseNetworkModel()
