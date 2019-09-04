package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.codingblocks.cbonlineapp.database.models.CourseContent
import com.codingblocks.cbonlineapp.database.models.SectionWithContent

@Dao
interface SectionWithContentsDao {

//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    fun insert(join: SectionWithContent)

//    @Query("""
//        SELECT * FROM CourseContent
//        INNER JOIN sectionwithcontent ON
//        CourseContent.ccid = sectionwithcontent.content_id
//        WHERE sectionwithcontent.section_id = :sectionID ORDER BY `order`
//        """)
//    fun getContentWithSectionId(sectionID: String): LiveData<List<CourseContent>>
//
//    @Query("""
//        SELECT * FROM CourseContent cc
//        INNER JOIN sectionwithcontent swc ON
//        cc.ccid = swc.content_id
//        WHERE swc.section_id = :sectionID AND cc.contentable = "lecture"
//        AND isDownloaded = "false" ORDER BY `order`
//        """)
//    fun getVideoIdsWithSectionId(sectionID: String): LiveData<List<CourseContent>>
//
//    @Query("""
//        SELECT * FROM  CourseSection s
//	    INNER JOIN SectionWithContent sc ON sc."section_id" = s."csid"
//	    INNER JOIN CourseContent c ON c."ccid" = sc."content_id"
//	    WHERE s.attemptId = :attemptId AND progress = "UNDONE"
//        ORDER BY s."sectionOrder", sc."order" LIMIT 1
//        """)
//    fun resumeCourse(attemptId: String): LiveData<List<CourseContent>>

    @Query("""
        SELECT * FROM  CourseSection s,CourseContent cc
	    WHERE s.attemptId = :attemptId AND progress = "UNDONE"
        ORDER BY s."sectionOrder", cc."order" LIMIT 1
        """)
    fun resumeCourse(attemptId: String): LiveData<List<CourseContent>>

    @Transaction
    @Query("""
        SELECT * FROM CourseSection s
	    WHERE s.attemptId = :attemptId
        ORDER BY s."sectionOrder"
        """)
    fun getSectionWithContent(attemptId: String): DataSource.Factory<Int, SectionWithContent>
}
