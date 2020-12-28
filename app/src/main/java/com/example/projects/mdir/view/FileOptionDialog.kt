package com.example.projects.mdir.view

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import com.example.projects.R
import com.example.projects.databinding.LayoutFileOptionBinding
import com.example.projects.mdir.FileViewModel
import com.example.projects.mdir.data.FileItemEx
import com.google.android.material.bottomsheet.BottomSheetDialog

class FileOptionDialog(context: Context, val viewModel: FileViewModel, val file: FileItemEx) : BottomSheetDialog(context) {

    init {
        val binding = LayoutFileOptionBinding.bind(
            View.inflate(context, R.layout.layout_file_option, null)).apply {
            vm = viewModel
            item = file
            image = context.getDrawable(file.exType.drawableRes)
        }
        setContentView(binding.root)
        binding.tvName.isSelected = true // name marquee
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.attributes?.width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 360f, context.resources.displayMetrics).toInt()
        window?.attributes?.windowAnimations = R.style.BottomSheetAnimation
    }
}