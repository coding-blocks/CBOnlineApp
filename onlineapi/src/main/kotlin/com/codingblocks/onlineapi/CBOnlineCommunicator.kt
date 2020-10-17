package com.codingblocks.onlineapi

interface CBOnlineCommunicator {
    var authJwt: String
    var refreshToken: String
    var baseUrl: String
    var appVersion: Int
}
