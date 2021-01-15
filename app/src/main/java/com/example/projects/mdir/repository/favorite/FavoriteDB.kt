package com.example.projects.mdir.repository.favorite

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [(FavoriteData::class)], version = 1)
abstract class FavoriteDB : RoomDatabase() {
    abstract fun favoriteDao() : FavoriteDao
}