package com.example.projects.mdir.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projects.mdir.repository.SortBy
import com.example.projects.mdir.repository.SortOrder

class SortViewModel : ViewModel() {

    private val _sortF = MutableLiveData<SortBy>()
    val sortF: LiveData<SortBy>
        get() = _sortF

    private val _orderF = MutableLiveData<SortOrder>()
    val orderF: LiveData<SortOrder>
        get() = _orderF

    private val _sortS = MutableLiveData<SortBy>()
    val sortS: LiveData<SortBy>
        get() = _sortS

    private val _orderS = MutableLiveData<SortOrder>()
    val orderS: LiveData<SortOrder>
        get() = _orderS

}