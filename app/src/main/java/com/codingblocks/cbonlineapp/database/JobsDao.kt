package com.codingblocks.cbonlineapp.database

import androidx.room.Dao
import com.codingblocks.cbonlineapp.database.models.JobsModel

@Dao
abstract class JobsDao : BaseDao<JobsModel> {

}
