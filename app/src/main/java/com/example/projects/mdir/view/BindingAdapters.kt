package com.example.projects.mdir.view

import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.view.ContextThemeWrapper
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.projects.R
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
    @BindingAdapter("items", "viewModel", "lifecycleOwner")
    fun setItems(recyclerView: RecyclerView, items: List<FileItemEx>?, viewModel: FileViewModel, lifecycleOwner: LifecycleOwner) {
        (recyclerView.adapter as BaseAdapter).run {
            items?.let {
                setFileItems(it)
                it.indices.forEach { position ->
                    it[position].liveDrawable.observe(lifecycleOwner ,Observer {
                        recyclerView.adapter?.notifyItemChanged(position)
                    })
                }
            }
                // BrowserFragment onCreateView에서 진행
//            } ?: run{
//                viewModel.loadDirectory()
        }
    }

    @JvmStatic
    @BindingAdapter("depthList", "viewModel")
    fun setDepthList(linearLayout: LinearLayout, depths: List<String>?, viewModel: FileViewModel) {
//        if(depths == null) return
        depths ?: return
        linearLayout.removeAllViews()

        var path = ""
        depths.forEach { nameDir ->
            val symbolContext = ContextThemeWrapper(linearLayout.context, R.style.BrowserDepthSymbol)
            linearLayout.addView(AppCompatTextView(symbolContext))

            TransitionManager.beginDelayedTransition(linearLayout, ChangeBounds().apply {
                duration = 500L
            })

            path = "$path/$nameDir"
            val buttonContext = ContextThemeWrapper(linearLayout.context, R.style.BrowserDepthTextView)
            linearLayout.addView(AppCompatTextView(buttonContext).apply {
                text = nameDir
                tag = path
                setOnClickListener { viewModel.requestDepth(it.tag.toString()) }
            })
        }

        return
    }

}