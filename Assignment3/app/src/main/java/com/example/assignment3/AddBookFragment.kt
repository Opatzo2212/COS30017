package com.example.assignment3

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.load
import java.io.File
import androidx.core.net.toUri

class AddBookFragment : Fragment() {

    private lateinit var viewModel: BookViewModel
    private lateinit var ivCover: ImageView
    private var selectedImageUri: Uri? = null
    private var existingImageUriString: String? = null
    private var bookIdToEdit: Int = -1

    private val statusOptions = arrayOf("Plan to Read", "Reading", "Completed", "Dropped")

    companion object {
        fun newInstance(bookId: Int = -1): AddBookFragment {
            val fragment = AddBookFragment()
            val args = Bundle()
            args.putInt("BOOK_ID", bookId)
            fragment.arguments = args
            return fragment
        }
    }

    private val selectImageResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            ivCover.load(it) {
                crossfade(true)
                placeholder(android.R.drawable.ic_menu_gallery)
            }
        }
    }

    private fun copyUriToInternalStorage(context: Context, uri: Uri): String? {
        val contentResolver = context.contentResolver

        contentResolver.openInputStream(uri)?.use { inputStream ->
            val fileName = "cover_${System.currentTimeMillis()}.png"
            val internalFile = File(context.filesDir, fileName)

            internalFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }

            return internalFile.absolutePath
        }
        return null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_book, container, false)
    }

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<View>(R.id.etSearch).visibility = View.GONE
        requireActivity().findViewById<View>(R.id.sortLayout).visibility = View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[BookViewModel::class.java]
        bookIdToEdit = arguments?.getInt("BOOK_ID", -1) ?: -1

        ivCover = view.findViewById(R.id.ivCover)
        val etNovelName = view.findViewById<EditText>(R.id.etNovelName)
        val etAuthor = view.findViewById<EditText>(R.id.etAuthor)
        val etTags = view.findViewById<EditText>(R.id.etTags)
        val etLink = view.findViewById<EditText>(R.id.etLink)
        val spinnerStatus = view.findViewById<Spinner>(R.id.spinnerStatus)
        val etChapter = view.findViewById<EditText>(R.id.etChapter)
        val etDescription = view.findViewById<EditText>(R.id.etDescription)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statusOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus.adapter = adapter

        spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                (view as? TextView)?.let { tv ->
                    when (statusOptions[position]) {
                        "Plan to Read" -> tv.setTextColor(Color.WHITE)
                        "Reading" -> tv.setTextColor(Color.parseColor("#FFD700"))
                        "Completed" -> tv.setTextColor(Color.parseColor("#4CAF50"))
                        "Dropped" -> tv.setTextColor(Color.parseColor("#F44336"))
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        if (bookIdToEdit != -1) {
            btnSave.text = "UPDATE NOVEL"
            viewModel.getBookById(bookIdToEdit).observe(viewLifecycleOwner) { book ->
                book?.let {
                    etNovelName.setText(it.title)
                    etAuthor.setText(it.author)
                    etTags.setText(it.tags)
                    etLink.setText(it.link)
                    etChapter.setText(it.currentChapter.toString())
                    etDescription.setText(it.description)
                    existingImageUriString = it.coverImageUri

                    val spinnerPosition = adapter.getPosition(it.status)
                    spinnerStatus.setSelection(spinnerPosition)

                    it.coverImageUri?.let { uriString ->
                        ivCover.load(uriString.toUri()) {
                            crossfade(true)
                            placeholder(android.R.drawable.ic_menu_camera)
                        }
                    }
                }
            }
        }

        btnCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        ivCover.setOnClickListener {
            selectImageResult.launch("image/*")
        }

        btnSave.setOnClickListener {
            val name = etNovelName.text.toString()
            val author = etAuthor.text.toString()
            val tags = etTags.text.toString()
            val linkText = etLink.text.toString()
            val statusText = spinnerStatus.selectedItem.toString()
            val chapterText = etChapter.text.toString().toIntOrNull() ?: 0
            val descriptionText = etDescription.text.toString()

            val persistentFilePath = if (selectedImageUri != null) {
                copyUriToInternalStorage(requireContext(), selectedImageUri!!) ?: ""
            } else {
                existingImageUriString ?: ""
            }

            if (name.isBlank()) {
                Toast.makeText(requireContext(), "Please enter a novel name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
            val currentUserId = sharedPref.getInt("USER_ID", -1)

            val bookToSave = Book(
                id = if (bookIdToEdit != -1) bookIdToEdit else 0,
                userId = currentUserId,
                title = name,
                author = author,
                tags = tags,
                link = linkText,
                description = descriptionText,
                coverImageUri = persistentFilePath,
                status = statusText,
                currentChapter = chapterText
            )

            if (bookIdToEdit != -1) {
                viewModel.updateBook(bookToSave)
            } else {
                viewModel.insertBook(bookToSave)
            }

            parentFragmentManager.popBackStack()
        }
    }
}