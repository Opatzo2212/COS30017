package com.example.assignment3

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BookRepository

    init {
        val bookDao = BookDatabase.getDatabase(application).bookDao()
        repository = BookRepository(bookDao)
    }
    
    fun getAllBooksForUser(userId: Int): LiveData<List<Book>> {
        return repository.getAllBooks(userId)
    }

    fun insertBook(book: Book) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(book)
    }

    fun updateBook(book: Book) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(book)
    }

    fun deleteBook(book: Book) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(book)
    }

    fun getBookById(id: Int): LiveData<Book> {
        return repository.getBookById(id)
    }
}