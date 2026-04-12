package com.example.assignment3

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LibraryFragment : Fragment() {

    private lateinit var viewModel: BookViewModel
    private lateinit var adapter: BookAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvLibrary)

        adapter = BookAdapter(
            onBookClick = { clickedBook ->
                val detailFragment = BookDetailFragment.newInstance(clickedBook.id)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, detailFragment)
                    .addToBackStack(null)
                    .commit()
            },
            onAddClick = {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, AddBookFragment())
                    .addToBackStack(null)
                    .commit()
            }
        )

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView.adapter = adapter

        val etSearch = requireActivity().findViewById<EditText>(R.id.etSearch)
        val tvSortRecent = requireActivity().findViewById<TextView>(R.id.tvSortRecent)
        var isRecent = true

        tvSortRecent.setOnClickListener {
            isRecent = !isRecent
            tvSortRecent.text = if (isRecent) "Recent" else "A-Z"
            adapter.sort(isRecent)
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val currentUserId = sharedPref.getInt("USER_ID", -1)

        viewModel = ViewModelProvider(this)[BookViewModel::class.java]

        viewModel.getAllBooksForUser(currentUserId).observe(viewLifecycleOwner) { books ->
            adapter.setData(books)
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<View>(R.id.etSearch).visibility = View.VISIBLE
        requireActivity().findViewById<View>(R.id.sortLayout).visibility = View.VISIBLE
    }
}