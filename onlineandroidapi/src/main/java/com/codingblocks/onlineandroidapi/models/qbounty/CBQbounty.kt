package com.codingblocks.onlineandroidapi.models.qbounty

import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.Type

open class BaseModel {
    @Id
    val id: String = ""
}
// Use @JsonProperty when working with non camelcase fields

@Type("users")
class User: BaseModel() {
    @JvmField val username: String? = null
}

@Type("claims")
open class Claim: BaseModel() {
    @JvmField val description: String? = null
    @JvmField val link: String? = null
    @Relationship("claimant", resolve = true)
    @JvmField val claimant: User? = null
}

@Type("tasks")
open class Task: BaseModel() {
    @JvmField val title: String? = null
    @JvmField val description: String? = null
    @JvmField val instances: Int? = null
    @Relationship("owner")
    @JvmField val owner: User? = null
}