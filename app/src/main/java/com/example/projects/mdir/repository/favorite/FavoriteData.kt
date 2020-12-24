package com.example.projects.mdir.repository.favorite

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Favorite")
data class FavoriteData(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "isDir")
    val isDir: Boolean,

    @ColumnInfo(name = "time")
    val time: Long
)