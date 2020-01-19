package com.codingblocks.cbonlineapp.database

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDao<T> {

    /**
     * Insert an object in the database with Conflict Strategy IGNORE.
     *
     * @param obj the object to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(obj: T): Long

    /**
     * Insert an object in the database with Conflict Strategy REPLACE.
     *
     * @param obj the object to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNew(obj: T)

    /**
     * Insert an list of object in the database.
     *
     * @param obj the object to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(obj: ArrayList<T>)

    /**
     * Insert an object in the database and returns its id.
     *
     * @param obj the objects to be inserted.
     */
    @Insert
    fun insertWithId(obj: T): Long

    /**
     * Update an object from the database.
     *
     * @param obj the object to be updated
     */
    @Update
    suspend fun update(obj: T)

    /**
     * Delete an object from the database
     *
     * @param obj the object to be deleted
     */
    @Delete
    fun delete(obj: T)
}
