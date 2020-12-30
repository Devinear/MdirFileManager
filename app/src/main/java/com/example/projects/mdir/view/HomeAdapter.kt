package com.example.projects.mdir.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projects.databinding.ItemRecentBinding
import com.example.projects.mdir.data.FileItemEx

class HomeAdapter : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    val items = mutableListOf<FileItemEx>()

    class ViewHolder(private val binding : ItemRecentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: FileItemEx) {
            binding.ivImage.setImageDrawable(item.liveDrawable.value)
            binding.tvName.text = item.simpleName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    = ViewHolder(ItemRecentBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    = holder.onBind(item = items[position])
    
    override fun getItemCount(): Int = items.count()

    fun setItems(list: List<FileItemEx>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

}