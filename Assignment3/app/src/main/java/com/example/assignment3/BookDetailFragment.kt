package com.example.assignment3

import android.app.AlertDialog
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.load
import androidx.core.net.toUri

class BookDetailFragment : Fragment() {

    private lateinit var viewModel: BookViewModel
    private var bookId: Int = -1
    private var currentBook: Book? = null

    companion object {
        fun newInstance(id: Int): BookDetailFragment {
            val fragment = BookDetailFragment()
            val args = Bundle()
            args.putInt("BOOK_ID", id)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_book_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bookId = arguments?.getInt("BOOK_ID") ?: -1
        viewModel = ViewModelProvider(this)[BookViewModel::class.java]

        val btnBack = view.findViewById<View>(R.id.btnBack)
        val btnDelete = view.findViewById<ImageButton>(R.id.btnDelete)
        val btnEdit = view.findViewById<ImageButton>(R.id.btnEdit)
        val ivCover = view.findViewById<ImageView>(R.id.ivCoverDetail)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitleDetail)
        val tvAuthor = view.findViewById<TextView>(R.id.tvAuthorDetail)
        val tvTags = view.findViewById<TextView>(R.id.tvTagsDetail)
        val tvLink = view.findViewById<TextView>(R.id.tvLinkDetail)
        val tvStatus = view.findViewById<TextView>(R.id.tvStatusDetail)
        val tvChapter = view.findViewById<TextView>(R.id.tvChapterDetail)
        val tvDescription = view.findViewById<TextView>(R.id.tvDescriptionDetail)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnEdit.setOnClickListener {
            val editFragment = AddBookFragment.newInstance(bookId)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, editFragment)
                .addToBackStack(null)
                .commit()
        }

        btnDelete.setOnClickListener {
            currentBook?.let { bookToDelete ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Novel")
                    .setMessage("Are you sure you want to delete '${bookToDelete.title}'? This cannot be undone.")
                    .setPositiveButton("DELETE") { dialog, which ->
                        viewModel.deleteBook(bookToDelete)
                        parentFragmentManager.popBackStack()
                    }
                    .setNegativeButton("CANCEL") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }

        if (bookId != -1) {
            viewModel.getBookById(bookId).observe(viewLifecycleOwner) { book ->
                book?.let {
                    currentBook = it

                    tvTitle.text = it.title
                    tvAuthor.text = "Author: ${it.author}"
                    tvTags.text = "Tags: ${it.tags}"

                    tvStatus.text = "Status: ${it.status}"
                    when (it.status) {
                        "Plan to Read" -> tvStatus.setTextColor(Color.WHITE)
                        "Reading" -> tvStatus.setTextColor(Color.parseColor("#FFD700"))
                        "Completed" -> tvStatus.setTextColor(Color.parseColor("#4CAF50"))
                        "Dropped" -> tvStatus.setTextColor(Color.parseColor("#F44336"))
                        else -> tvStatus.setTextColor(Color.WHITE)
                    }

                    tvChapter.text = "Chapter: ${it.currentChapter}"

                    if (it.link.isNotBlank()) {
                        tvLink.text = "Link: ${it.link}"
                        tvLink.visibility = View.VISIBLE
                    } else {
                        tvLink.text = "Link: None"
                        tvLink.visibility = View.VISIBLE
                    }

                    if (it.description.isNotBlank()) {
                        tvDescription.text = "Description:\n${it.description}"
                    } else {
                        tvDescription.text = "Description:\nNo description provided."
                    }

                    it.coverImageUri?.let { uriString ->
                        ivCover.load(uriString.toUri(       )) {
                            crossfade(true)
                            placeholder(android.R.drawable.ic_menu_gallery)
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<View>(R.id.etSearch).visibility = View.GONE
        requireActivity().findViewById<View>(R.id.sortLayout).visibility = View.GONE
    }
}