package com.codingblocks.cbonlineapp

import tech.arnav.spork.annotations.Pref
import tech.arnav.spork.annotations.PreferenceFile

@PreferenceFile("app_prefs")
interface AppPrefs {

    @Pref("userId")
    var userId: String

    @Pref("oneAuthId")
    var oneAuthId: String

    @Pref("userImage")
    var userImage: String

    @Pref("firstName")
    var firstName: String

    @Pref("lastName")
    var lastName: String

    @Pref("roleId")
    var roleId: Int

}
