package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomWarnings
import com.codingblocks.cbonlineapp.database.models.CourseContent
import com.codingblocks.cbonlineapp.database.models.SectionWithContent

@Dao
interface SectionWithContentsDao {

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("""
        SELECT * FROM CourseContent
        INNER JOIN sectionwithcontent ON
        CourseContent.id = sectionwithcontent.content_id
        WHERE sectionwithcontent.section_id = :sectionID ORDER BY `order`
        """)
    fun getContentWithSectionId(sectionID: String): LiveData<List<CourseContent>>

    @Query("""
        SELECT * FROM CourseContent
        INNER JOIN sectionwithcontent ON
        CourseContent.id = sectionwithcontent.content_id
        WHERE sectionwithcontent.section_id = :sectionID AND contentable = "lecture" AND isDownloaded = 0 ORDER BY `order`
        """)
    fun getVideoIdsWithSectionId(sectionID: String): LiveData<List<CourseContent>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(join: SectionWithContent)
}
