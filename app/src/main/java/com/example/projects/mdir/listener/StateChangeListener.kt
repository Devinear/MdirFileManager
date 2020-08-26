package com.example.projects.mdir.listener

interface StateChangeListener {
    fun notifyPath(path: String)
    fun notifyDirCount(count: Int)
    fun notifyFileCount(count: Int)
    fun notifyImageCount(count: Int)
}