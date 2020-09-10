package com.codingblocks.onlineapi.models

import com.github.jasminb.jsonapi.annotations.Id

/** Base class for JSON-API  */
open class BaseNetworkModel {
    @Id
    var id: String = ""
    var updatedAt: String = ""
}
