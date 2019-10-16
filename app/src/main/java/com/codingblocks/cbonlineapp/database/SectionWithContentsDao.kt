package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.codingblocks.cbonlineapp.database.models.SectionContentHolder

@Dao
interface SectionWithContentsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(join: SectionContentHolder.SectionWithContent)

    //    @Query("""
//        SELECT * FROM ContentModel
//        INNER JOIN sectionwithcontent ON
//        ContentModel.ccid = sectionwithcontent.content_id
//        WHERE sectionwithcontent.section_id = :sectionID ORDER BY `order`
//        """)
//    fun getContentWithSectionId(sectionID: String): LiveData<List<ContentModel>>
//
//    @Query("""
//        SELECT * FROM ContentModel cc
//        INNER JOIN sectionwithcontent swc ON
//        cc.ccid = swc.content_id
//        WHERE swc.section_id = :sectionID AND cc.contentable = "lecture"
//        AND isDownloaded = "false" ORDER BY `order`

    //        """)
//    fun getVideoIdsWithSectionId(sectionID: String): LiveData<List<ContentModel>>
//
    @Transaction
    @Query("""
        SELECT s.*,c.* FROM SectionModel s
	    INNER JOIN SectionWithContent sc ON sc."section_id" = s."csid"
	    INNER JOIN ContentModel c ON c."ccid" = sc."content_id"
	    WHERE s.attemptId = :attemptId AND progress = "UNDONE"
        ORDER BY s."sectionOrder",sc.`order` LIMIT 1
        """)
    fun resumeCourse(attemptId: String): LiveData<List<SectionContentHolder.SectionContentPair>>

    @Transaction
    @Query("""
        SELECT s.* FROM SectionModel s
	    WHERE s.attemptId = :attemptId
        ORDER BY s."sectionOrder"
        """)
    fun getSectionWithContent(attemptId: String): LiveData<List<SectionContentHolder.SectionContentPair>>
}
