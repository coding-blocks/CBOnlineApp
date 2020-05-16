package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.codingblocks.cbonlineapp.database.models.CourseInstructorPair
import com.codingblocks.cbonlineapp.database.models.CourseRunPair
import com.codingblocks.cbonlineapp.database.models.CourseWithInstructor

@Dao
interface CourseWithInstructorDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(join: CourseWithInstructor)

//
//    @Transaction
//    @Query("""
//       SELECT c.*,r.* FROM RunModel r
// 	   INNER JOIN CourseModel c ON c.cid = r.crCourseId
//       WHERE r.crCourseId IN (:courses) AND r.crAttemptId IS NULL
//    """)
//    fun getJobCourses(courses: ArrayList<String>): LiveData<List<CourseInstructorPair>>

    @Query("""
    SELECT rA.*,r.*,c.* FROM  RunAttemptModel rA
 	   INNER JOIN RunModel r ON r.crUid = rA.runId
       INNER JOIN CourseModel c ON c.cid = r.crCourseId
       ORDER BY rA.lastAccessedAt DESC
    """)
    fun getMyRuns(): LiveData<List<CourseInstructorPair>>

    @Query("""
    SELECT rA.*,r.*,c.* FROM  RunAttemptModel rA
 	   INNER JOIN RunModel r ON r.crUid = rA.runId
       INNER JOIN CourseModel c ON c.cid = r.crCourseId
       WHERE rA.premium = 1  ORDER BY rA.lastAccessedAt DESC
    """)
    fun getPurchasesRuns(): LiveData<List<CourseInstructorPair>>

    @Query("""
    SELECT rA.*,r.*,c.* FROM  RunAttemptModel rA
 	   INNER JOIN RunModel r ON r.crUid = rA.runId
       INNER JOIN CourseModel c ON c.cid = r.crCourseId
       WHERE rA.premium = 1 AND rA.`end` > :currenttimeSec AND rA.runTier != "LITE"
       ORDER BY rA.lastAccessedAt DESC
    """)
    fun getActiveRuns(currenttimeSec: Long): LiveData<List<CourseInstructorPair>>

    @Query("""
    SELECT rA.*,r.*,c.* FROM  RunAttemptModel rA
 	   INNER JOIN RunModel r ON r.crUid = rA.runId
       INNER JOIN CourseModel c ON c.cid = r.crCourseId
       ORDER BY rA.lastAccessedAt DESC LIMIT 5
    """)
    fun getRecentRuns(): LiveData<List<CourseInstructorPair>>

    @Query("""
    SELECT rA.*,r.*,c.* FROM  RunAttemptModel rA
 	   INNER JOIN RunModel r ON r.crUid = rA.runId
       INNER JOIN CourseModel c ON c.cid = r.crCourseId
       WHERE rA.`end` < :currentTimeSec
    """)
    fun getExpiredRuns(currentTimeSec: Long): LiveData<List<CourseInstructorPair>>

    @Query("""
   SELECT rA.*,r.*,c.* FROM  RunAttemptModel rA
 	   INNER JOIN RunModel r ON r.crUid = rA.runId
       INNER JOIN CourseModel c ON c.cid = r.crCourseId
       ORDER BY rA.lastAccessedAt DESC LIMIT 1
    """)
    fun getTopRun(): LiveData<CourseRunPair>

    @Query("""
   SELECT rA.*,r.*,c.* FROM  RunAttemptModel rA
 	   INNER JOIN RunModel r ON r.crUid = rA.runId
       INNER JOIN CourseModel c ON c.cid = r.crCourseId
       WHERE rA.attemptId = :id ORDER BY rA.lastAccessedAt DESC LIMIT 1
    """)
    fun getRunById(id: String): LiveData<CourseRunPair>
}
