package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.codingblocks.cbonlineapp.database.models.CommentModel

@Dao
abstract class CommentsDao : BaseDao<CommentModel> {

    @Query("SElECT * FROM CommentModel where dbtId = :id")
    abstract fun getComments(id: String): LiveData<List<CommentModel>>
}
