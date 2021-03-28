package com.example.projects.mdir.listener

interface RequestListener {
    fun onRequestStoragePath(path: String)
    fun onRequestProgressBar(show: Boolean)
}