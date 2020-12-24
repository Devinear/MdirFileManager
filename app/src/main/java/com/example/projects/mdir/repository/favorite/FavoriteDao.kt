package com.example.projects.mdir.repository.favorite

import androidx.room.*

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInfo(vararg data: FavoriteData)

    @Delete
    suspend fun deleteInfo(vararg data: FavoriteData)

    @Query("SELECT * FROM Favorite")
    suspend fun getAll(): List<FavoriteData>

    @Query("DELETE FROM Favorite")
    suspend fun deleteAll()

    @Update
    suspend fun update(vararg data: FavoriteData)
}