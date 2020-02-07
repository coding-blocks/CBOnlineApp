package com.codingblocks.cbonlineapp.database.converters

import androidx.room.TypeConverter
import java.sql.Date

class TimestampConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
