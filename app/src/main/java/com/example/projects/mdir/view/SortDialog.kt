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
import com.example.projects.mdir.common.Sort
import com.example.projects.mdir.repository.SortBy
import com.example.projects.mdir.repository.SortOrder

class SortDialog(context: Context, val viewModel: FileViewModel) : Dialog(context)/*, ViewModelStoreOwner*/ {

    private val binding : LayoutSortBinding
        = LayoutSortBinding.bind(View.inflate(context, R.layout.layout_sort, null))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fir = Sort.sortPairFir
        val sec = Sort.sortPairSec
        with(binding) {
            spFirSort.adapter = SortAdapter(context, SortBy.values())
            spFirSort.setSelection(fir.first.position)
            spFirOrder.adapter = SortAdapter(context, SortOrder.values())
            spFirOrder.setSelection(fir.second.position)

            spSecSort.adapter = SortAdapter(context, SortBy.values())
            spSecSort.setSelection(sec.first.position)
            spSecOrder.adapter = SortAdapter(context, SortOrder.values())
            spSecOrder.setSelection(sec.second.position)

            dialog = this@SortDialog
        }

        setContentView(binding.root)
    }

    fun onClickOk() {
        Sort.sortPairFir =
                Pair(getSortBy(binding.spFirSort.selectedItemPosition), getSortOrder(binding.spFirOrder.selectedItemPosition))
        Sort.sortPairSec =
                Pair(getSortBy(binding.spSecSort.selectedItemPosition), getSortOrder(binding.spSecOrder.selectedItemPosition))

        viewModel.requestRefreshSort()

        dismiss()
    }

    private fun getSortBy(position: Int) : SortBy
    = when(position) {
        SortBy.Name.position -> SortBy.Name
        SortBy.Type.position -> SortBy.Type
        SortBy.Size.position -> SortBy.Size
        SortBy.Date.position -> SortBy.Date
        else -> SortBy.Favorite
    }

    private fun getSortOrder(position: Int) : SortOrder
    = when(position) {
        SortOrder.Ascending.position -> SortOrder.Ascending
        else -> SortOrder.Descending
    }

    fun onClickCancel() {
        dismiss()
    }
}