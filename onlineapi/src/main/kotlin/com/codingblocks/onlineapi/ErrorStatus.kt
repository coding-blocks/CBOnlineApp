package com.codingblocks.onlineapi

/**
 * various error status to know what happened if something goes wrong with a repository call
 */
class ErrorStatus {
    //    /**
//     * error in getting value (Json Error, Server Error, etc)
//     */
//    BAD_RESPONSE,
//    /**
//     * Time out  error
//     */
//    TIMEOUT,
//    /**
//     * no data available in repository
//     */
//    EMPTY_RESPONSE,
//    /**
//     * an unexpected error
//     */
//    NOT_DEFINED,
//    /**
//     * bad credential
//     */
//    UNAUTHORIZED
    companion object {
        const val NO_CONNECTION = "Not Connected To Internet"
        const val UNAUTHORIZED = "You are Unauthorized to View This Page"
        const val NOT_DEFINED = "Please Report Bug"
        const val TIMEOUT = "Request has been Timed out"
        const val EMPTY_RESPONSE = "no data available in repository"


    }
}
