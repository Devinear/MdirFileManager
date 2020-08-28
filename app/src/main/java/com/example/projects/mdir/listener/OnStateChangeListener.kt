package com.example.projects.mdir.listener

interface OnStateChangeListener {
    fun notifyPath(path: String)
    fun notifyDirCount(count: Int)
    fun notifyFileCount(count: Int)
    fun notifyImageCount(count: Int)
}