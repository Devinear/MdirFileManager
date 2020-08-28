package com.example.projects.mdir

import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableArrayList
import androidx.recyclerview.widget.RecyclerView

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("bind:item")
    fun bindItem(recyclerView: RecyclerView, items: ObservableArrayList<FileItem>) {
        val adapter : FileAdapter = recyclerView.adapter as FileAdapter
        adapter.setFileItems(items)
    }
}