package com.example.projects.mdir.view

import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableArrayList
import androidx.recyclerview.widget.RecyclerView
import com.example.projects.mdir.FileViewModel
import com.example.projects.mdir.data.FileItemEx
import com.example.projects.mdir.view.base.BaseAdapter

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("bind:item")
    fun bindItem(recyclerView: RecyclerView, items: ObservableArrayList<FileItemEx>) {
        (recyclerView.adapter as BaseAdapter).apply {
            setFileItems(items)
        }
    }

    @JvmStatic
    @BindingAdapter("items", "viewModel")
    fun setItems(recyclerView: RecyclerView, items: ObservableArrayList<FileItemEx>, viewModel: FileViewModel) {
        (recyclerView.adapter as BaseAdapter).apply {
            setFileItems(items)
        }
    }
}