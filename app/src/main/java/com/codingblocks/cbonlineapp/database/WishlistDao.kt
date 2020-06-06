package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.codingblocks.cbonlineapp.database.models.WishlistModel

@Dao
interface WishlistDao : BaseDao<WishlistModel>{

    @Query("SElECT * FROM WishlistModel where cid = :id LIMIT 1")
    suspend fun getWishlistsByCourse(id: String): WishlistModel

    @Query("SELECT * FROM WishlistModel ")
    fun getAllWishlists() : LiveData<List<WishlistModel>>

    @Query("SELECT * FROM WishlistModel LIMIT 3")
    fun getWishlistThree() : LiveData<List<WishlistModel>>

    @Query("DELETE FROM WishlistModel")
    suspend fun deleteAll()

    @Query("DELETE FROM WishlistModel WHERE cid = :id")
    suspend fun deleteCourseID(id: String)
}
