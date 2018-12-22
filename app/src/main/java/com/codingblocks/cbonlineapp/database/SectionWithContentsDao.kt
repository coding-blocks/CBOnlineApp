package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.*

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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(join: SectionWithContent)

}