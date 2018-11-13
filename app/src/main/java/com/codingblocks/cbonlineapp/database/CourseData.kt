package com.codingblocks.cbonlineapp.database

import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courseData")
data class CourseData(@PrimaryKey(autoGenerate = true) var id: Long?,
                      @ColumnInfo(name = "thumbnailUrl") var thumbnailUrl: String,
                      @ColumnInfo(name = "title") var title: String,
                      @ColumnInfo(name = "seen") var seen: Boolean,
                      @ColumnInfo(name = "type") var type: String,
                      @Nullable @ColumnInfo(name = "externalLink") var url: String,
                      @Nullable @ColumnInfo(name = "topic") var topic: String,
                      @Nullable @ColumnInfo(name = "course") var course: String,
                      @Nullable @ColumnInfo(name = "videotitle") var videotitle: String,
                      @Nullable @ColumnInfo(name = "videoId") var videoId: String,
                      @Nullable @ColumnInfo(name = "description") var description: String,
                      @Nullable @ColumnInfo(name = "fragmentPosition") var fragmentPosition: Int


) {
    constructor() : this(null, "", "", false, "", "", "",
            "", "", "", "", 0)

}