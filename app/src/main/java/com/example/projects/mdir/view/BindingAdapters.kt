package com.example.projects.mdir.view

import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableArrayList
import androidx.recyclerview.widget.RecyclerView
import com.example.projects.mdir.data.FileItem
import com.example.projects.mdir.view.base.BaseAdapter

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("bind:item")
    fun bindItem(recyclerView: RecyclerView, items: ObservableArrayList<FileItem>) {
        (recyclerView.adapter as BaseAdapter).apply {
            setFileItems(items)
        }
    }
}