package com.codingblocks.cbonlineapp.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class InstructorModel(
    @PrimaryKey
    var uid: String,
    var name: String?,
    var description: String,
    var photo: String?,
    var email: String?,
    var sub: String?
)
