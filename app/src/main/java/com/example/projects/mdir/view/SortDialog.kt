package com.example.projects.mdir.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.projects.R
import com.example.projects.databinding.LayoutSortBinding
import com.example.projects.mdir.FileViewModel

class SortDialog(context: Context, val viewModel: FileViewModel) : Dialog(context) {

    private val binding : LayoutSortBinding = LayoutSortBinding.bind(
        View.inflate(context, R.layout.layout_sort, null)).apply {
        vm = viewModel
    }

    init {
        setContentView(binding.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observeViewModel()
    }

    override fun dismiss() {
        super.dismiss()
    }

    private fun observeViewModel() {

    }
}