package com.example.projects.mdir.view

import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableArrayList
import androidx.recyclerview.widget.RecyclerView
import com.example.projects.mdir.data.FileItem

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("bind:item")
    fun bindItem(recyclerView: RecyclerView, items: ObservableArrayList<FileItem>) {
        val adapter : FileAdapter = recyclerView.adapter as FileAdapter
        adapter.setFileItems(items)
    }
}