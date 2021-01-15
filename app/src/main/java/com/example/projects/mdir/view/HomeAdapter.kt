package com.example.projects.mdir.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.projects.databinding.ItemRecentBinding
import com.example.projects.mdir.data.FileItemEx

class HomeAdapter : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    val items = mutableListOf<FileItemEx>()

    class ViewHolder(private val binding : ItemRecentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: FileItemEx) {
            item.liveDrawable.value
                ?.run {
                    with(binding.ivImage) {
                        setImageDrawable(this@run)
                        scaleType = ImageView.ScaleType.CENTER_CROP
                    }
                }
                ?:run {
                    with(binding.ivImage) {
                        setImageResource(item.exType.drawableRes)
                        scaleType = ImageView.ScaleType.CENTER
                    }
                }
            binding.tvName.text = item.simpleName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    = ViewHolder(ItemRecentBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    = holder.onBind(item = items[position])
    
    override fun getItemCount(): Int = items.count()

    fun setItems(list: List<FileItemEx>, lifecycleOwner: LifecycleOwner) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()

        items.indices.forEach { position ->
            items[position].liveDrawable.value ?: items[position].liveDrawable.observe(lifecycleOwner, Observer {
                it?.run { notifyItemChanged(position) }
            })
        }
    }

}