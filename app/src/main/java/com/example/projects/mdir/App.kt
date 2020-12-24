package com.example.projects.mdir

import android.app.Application
import androidx.room.Room
import com.example.projects.mdir.repository.favorite.FavoriteDB

open class App : Application() {

    companion object {
        private lateinit var favorite : FavoriteDB

        val FAVORITE_DATABASE : FavoriteDB
            get() = favorite
    }

    override fun onCreate() {
        super.onCreate()
        favorite = Room.databaseBuilder(this, FavoriteDB::class.java, "favorite.db").build()
    }
}