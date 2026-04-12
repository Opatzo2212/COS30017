package com.example.assignment3

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load

class BookAdapter(
    private val onBookClick: (Book) -> Unit,
    private val onAddClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var fullBookList = emptyList<Book>()
    private var displayList = emptyList<Book>()
    private var currentQuery = ""
    private var isSortRecent = true

    private val viewTypeAdd = 0
    private val viewTypeBook = 1

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val coverImage: ImageView = itemView.findViewById(R.id.ivBookCoverGrid)
        val titleText: TextView = itemView.findViewById(R.id.tvBookTitleGrid)
    }

    class AddViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) viewTypeAdd else viewTypeBook
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == viewTypeAdd) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_add_book_grid, parent, false)
            AddViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book_grid, parent, false)
            BookViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AddViewHolder) {
            holder.itemView.setOnClickListener { onAddClick() }
        } else if (holder is BookViewHolder) {
            val currentBook = displayList[position - 1]
            holder.titleText.text = currentBook.title

            currentBook.coverImageUri?.let { uriString ->
                holder.coverImage.load(Uri.parse(uriString)) {
                    crossfade(true)
                    placeholder(android.R.drawable.ic_menu_gallery)
                }
            } ?: holder.coverImage.setImageResource(android.R.drawable.ic_menu_gallery)

            holder.itemView.setOnClickListener { onBookClick(currentBook) }
        }
    }

    override fun getItemCount(): Int {
        return displayList.size + 1
    }

    fun setData(books: List<Book>) {
        this.fullBookList = books
        applyFilters()
    }

    fun filter(query: String) {
        this.currentQuery = query
        applyFilters()
    }

    fun sort(recent: Boolean) {
        this.isSortRecent = recent
        applyFilters()
    }

    private fun applyFilters() {
        var temp = if (currentQuery.isBlank()) {
            fullBookList
        } else {
            fullBookList.filter {
                it.title.contains(currentQuery, ignoreCase = true) ||
                        it.author.contains(currentQuery, ignoreCase = true) ||
                        it.tags.contains(currentQuery, ignoreCase = true)
            }
        }

        temp = if (isSortRecent) {
            temp.sortedByDescending { it.id }
        } else {
            temp.sortedBy { it.title.lowercase() }
        }

        displayList = temp
        notifyDataSetChanged()
    }
}