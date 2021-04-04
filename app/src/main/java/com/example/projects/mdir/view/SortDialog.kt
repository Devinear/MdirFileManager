package com.example.projects.mdir.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.example.projects.R
import com.example.projects.databinding.LayoutSortBinding
import com.example.projects.mdir.FileManagerActivity
import com.example.projects.mdir.FileViewModel
import com.example.projects.mdir.repository.SortBy
import com.example.projects.mdir.repository.SortOrder

class SortDialog(context: Context, val viewModel: FileViewModel) : Dialog(context), ViewModelStoreOwner {

    private val sortViewModel = SortViewModel()
    private val binding : LayoutSortBinding
        = LayoutSortBinding.bind(View.inflate(context, R.layout.layout_sort, null))
        .apply { vm = sortViewModel }

    init {
        setContentView(binding.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.spFirSort.adapter = SortAdapter(context, SortBy.values())
        binding.spFirOrder.adapter = SortAdapter(context, SortOrder.values())

        binding.spSecSort.adapter = SortAdapter(context, SortBy.values())
        binding.spSecOrder.adapter = SortAdapter(context, SortOrder.values())

        observeViewModel()
    }

    override fun dismiss() {
        super.dismiss()
    }

    fun onClickOk() {
        dismiss()
    }

    fun onClickCancel() {
        dismiss()
    }

    override fun getViewModelStore(): ViewModelStore = viewModelStore

    private fun observeViewModel() {

    }
}