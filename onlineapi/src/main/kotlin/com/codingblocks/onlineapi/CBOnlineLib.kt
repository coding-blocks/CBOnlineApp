package com.codingblocks.onlineapi

object CBOnlineLib {
    private var clients: Clients? = null

    fun initialize(communicator: CBOnlineCommunicator) {
        clients = Clients(communicator)
    }

    private fun getClient() = checkNotNull(clients, { "CBOnlineLib.initialize() not called" })

    val api get() = getClient().api

    val hackapi get() = getClient().hackapi

    val onlineV2JsonApi get() = getClient().onlineV2JsonApi

    var httpLogging
        get() = getClient().getHttpLogging()
        set(value) = getClient().setHttpLogging(value)
}
