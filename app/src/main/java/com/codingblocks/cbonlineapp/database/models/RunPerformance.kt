package com.codingblocks.cbonlineapp.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.codingblocks.cbonlineapp.database.converters.ProgressItemConverter
import com.codingblocks.onlineapi.models.ProgressItem

/**
 * @author aggarwalpulkit596
 */
@Entity
data class RunPerformance(
    @PrimaryKey
    val id: String = "",
    val percentile: Int = 0,
    val remarks: String = "",
    @TypeConverters(ProgressItemConverter::class)
    val averageProgress: ArrayList<ProgressItem>,
    @TypeConverters(ProgressItemConverter::class)
    val userProgress: ArrayList<ProgressItem>
)
