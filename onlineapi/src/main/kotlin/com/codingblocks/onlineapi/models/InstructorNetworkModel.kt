package com.codingblocks.onlineapi.models

import com.github.jasminb.jsonapi.annotations.Type

/**
 * Data class for Instructor model
 * https://bitbucket.org/coding-blocks/amoeba-backend/src/a997903143f08d80af22e791204dbe2f1c800d3d/src/models/Instructor.js#lines-9
 */
@Type("instructors")
data class InstructorNetworkModel(
    val name: String?,
    val description: String?,
    val photo: String?,
    val email: String?,
    val sub: String?
) : BaseNetworkModel()
