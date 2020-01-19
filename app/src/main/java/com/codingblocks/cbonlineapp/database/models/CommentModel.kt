package com.codingblocks.cbonlineapp.database.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index("dbtId")],
    foreignKeys = [(ForeignKey(
        entity = DoubtsModel::class,
        parentColumns = ["dbtUid"],
        childColumns = ["dbtId"],
        onDelete = ForeignKey.CASCADE // or CASCADE
    ))]
)
class CommentModel(
    @PrimaryKey
    var id: String,
    var body: String,
    var dbtId: String,
    var updatedAt: String,
    var username: String
) {
    constructor() : this("", "", "", "", "")
}
