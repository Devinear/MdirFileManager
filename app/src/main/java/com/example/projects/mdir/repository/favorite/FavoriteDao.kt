package com.example.projects.mdir.repository.favorite

import androidx.room.*

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg data: FavoriteData)

    @Delete
    suspend fun delete(vararg data: FavoriteData)

    @Query("DELETE FROM Favorite WHERE id = :id")
    suspend fun deleteByPath(id: String)

    @Query("SELECT * FROM Favorite")
    suspend fun getAll(): List<FavoriteData>

    @Query("DELETE FROM Favorite")
    suspend fun deleteAll()

    @Update
    suspend fun update(vararg data: FavoriteData)
}