package com.yb.part4_chapter03

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yb.part4_chapter03.databinding.ItemSearchResultBinding
import com.yb.part4_chapter03.model.SearchResultEntity

class SearchRecyclerViewAdapter :
    RecyclerView.Adapter<SearchRecyclerViewAdapter.ViewHolder>() {

    private var searchResultList: List<SearchResultEntity> = listOf()
    private lateinit var searchResultClickListener: (SearchResultEntity) -> Unit

    inner class ViewHolder(
        private val binding: ItemSearchResultBinding,
        private val searchResultClickListener: (SearchResultEntity) -> Unit,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: SearchResultEntity) {
            binding.addressTextView.text = data.address
            binding.detailAddressTextView.text = data.detailAddress

            binding.root.setOnClickListener {
                searchResultClickListener(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            ItemSearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view, searchResultClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(searchResultList[position])
    }

    override fun getItemCount(): Int {
        return searchResultList.size
    }

    fun setSearchResultList(searchResultList: List<SearchResultEntity>, searchResultClickListener: (SearchResultEntity) -> Unit) {
        this.searchResultList = searchResultList
        this.searchResultClickListener = searchResultClickListener
        notifyDataSetChanged()
    }

}