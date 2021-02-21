package com.example.projects.mdir.view.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.GridLayoutManager
import com.example.projects.R
import com.example.projects.databinding.LayoutHomeBinding
import com.example.projects.mdir.FileManagerActivity
import com.example.projects.mdir.FileViewModel
import com.example.projects.mdir.common.FileType
import com.example.projects.mdir.data.FileItemEx
import com.example.projects.mdir.listener.RequestListener
import com.example.projects.mdir.view.HomeAdapter

class HomeFragment : Fragment() {

    private lateinit var binding : LayoutHomeBinding
    private lateinit var activity : Activity
    private lateinit var viewModel : FileViewModel
    var requestListener : RequestListener? = null

    private val activityViewModel : FileViewModel by lazy {
        ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                FileViewModel(requireActivity().application) as T
        }).get(FileViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = getActivity() as FileManagerActivity
        viewModel = ViewModelProvider(activity as ViewModelStoreOwner).get(FileViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.layout_home,
            container,
            false
        )
        binding.vm = activityViewModel
        binding.activity = activity as FileManagerActivity

        initUi()
        return binding.root
    }

    private fun initUi() {
        binding.reFavorite.adapter = HomeAdapter(fragment = this).apply {
            val favorites = viewModel.favorites
            val items = mutableListOf<FileItemEx>()
            favorites.forEach { favorite ->
                if(items.size >= FAVORITE_MAX_VISIBLE_COUNT)
                    return@forEach
                items.add(FileItemEx(favorite).apply { this.favorite.value = true })
            }
            setItems(items, this@HomeFragment)
            viewModel.requestThumbnailFavorite(items)
        }
        binding.reFavorite.layoutManager = GridLayoutManager(activity, 5)

        val favoriteSize = viewModel.favorites.size
        binding.laFavorite.visibility = if(favoriteSize > 0) View.VISIBLE else View.GONE
        binding.ibFavorite.visibility = if(favoriteSize > FAVORITE_MAX_VISIBLE_COUNT) View.VISIBLE else View.GONE
    }

    fun onFavorite(item: FileItemEx) {
        when (item.exType) {
            FileType.Dir -> { requestListener?.onRequestStoragePath(item.absolutePath) }
            else -> { viewModel.requestClickItem(item) }
        }
    }

    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            HomeFragment()
        }

        const val FAVORITE_MAX_VISIBLE_COUNT = 5
    }
}