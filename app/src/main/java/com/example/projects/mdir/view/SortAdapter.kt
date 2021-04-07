package com.example.projects.mdir.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.projects.R

class SortAdapter<T>(val context: Context, val items: Array<T>) : BaseAdapter() {

    override fun getCount(): Int = items.size

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(R.layout.item_spinner, null)
        view.findViewById<TextView>(R.id.text1).apply {
            text = items[position].toString()
        }
        return view
    }

    override fun getItem(position: Int): T = items[position]

    override fun getItemId(position: Int): Long = position.toLong()
}