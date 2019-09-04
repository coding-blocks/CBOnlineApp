package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.codingblocks.cbonlineapp.database.models.CourseContent

@Dao
abstract class ContentDao : BaseDao<CourseContent> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract override fun insert(obj: CourseContent)

    @Query("SElECT * FROM CourseContent ")
    abstract fun getContent(): LiveData<List<CourseContent>>

    @Query("SElECT * FROM CourseContent where attempt_id = :attempt_id ")
    abstract fun getCourseContents(attempt_id: String): LiveData<List<CourseContent>>

    @Query("SElECT * FROM CourseContent where attempt_id = :attempt_id AND ccid = :id")
    abstract fun getContentWithId(attempt_id: String, id: String): CourseContent

    @Query("SElECT * FROM CourseContent where isDownloaded = :progress ORDER BY date")
    abstract fun getDownloads(progress: String): List<CourseContent>

    @Query("SElECT * FROM CourseContent where attempt_id = :attempt AND section_id = :section")
    abstract fun getCourseSectionContents(attempt: String, section: String): LiveData<List<CourseContent>>

    @Query("UPDATE CourseContent SET isDownloaded = :downloadprogress WHERE lectureContentId = :contentid AND section_id = :section")
    abstract fun updateContent(section: String, contentid: String, downloadprogress: String)

    @Query("UPDATE CourseContent SET isDownloaded = :downloadprogress WHERE lectureId = :videoId AND section_id = :section")
    abstract fun updateContentWithVideoId(section: String, videoId: String, downloadprogress: String)

    @Query("UPDATE CourseContent SET progress = :progress AND progressId = :progressId WHERE ccid = :id AND attempt_id = :attemptId")
    abstract fun updateProgress(id: String, attemptId: String, progress: String, progressId: String)

    @Query("SELECT * FROM CourseContent WHERE section_id = :sectionId AND attempt_id =:attemptId AND `order` = ((SELECT `order` FROM CourseContent where ccid = :uid) + 1 ) LIMIT 1")
    abstract fun getNextItem(sectionId: String, attemptId: String, uid: String): LiveData<CourseContent>
}
