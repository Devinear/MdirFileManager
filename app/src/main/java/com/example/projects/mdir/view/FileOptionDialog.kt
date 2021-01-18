package com.example.projects.mdir.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.lifecycle.*
import com.example.projects.R
import com.example.projects.databinding.LayoutFileOptionBinding
import com.example.projects.mdir.FileViewModel
import com.example.projects.mdir.data.FileItemEx
import com.google.android.material.bottomsheet.BottomSheetDialog

class FileOptionDialog(context: Context, val viewModel: FileViewModel, val file: FileItemEx) : BottomSheetDialog(context), LifecycleOwner, LifecycleObserver {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val binding : LayoutFileOptionBinding = LayoutFileOptionBinding.bind(
        View.inflate(context, R.layout.layout_file_option, null)).apply {
        vm = viewModel
        item = file
        image = context.getDrawable(file.exType.drawableRes)
    }

    init {
        setContentView(binding.root)
        binding.tvName.isSelected = true // name marquee
    }

    override fun getLifecycle(): Lifecycle = lifecycleRegistry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.attributes?.width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 360f, context.resources.displayMetrics).toInt()
        window?.attributes?.windowAnimations = R.style.BottomSheetAnimation

        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        observeViewModel()
    }

    override fun dismiss() {
        super.dismiss()
        viewModel.requestShowOption(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun observeViewModel() {
        file.favorite.observe(this, Observer { favorite ->
            val res = if(favorite) R.drawable.baseline_star_white_24 else R.drawable.baseline_star_border_white_24
            binding.tvFavorite.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(res), null, null, null)
        })
    }
}