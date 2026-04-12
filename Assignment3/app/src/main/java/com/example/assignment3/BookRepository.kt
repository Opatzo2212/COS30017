package com.example.assignment3

import androidx.lifecycle.LiveData

class BookRepository(private val bookDao: BookDao) {

    fun getAllBooks(userId: Int): LiveData<List<Book>> {
        return bookDao.getAllBooks(userId)
    }

    suspend fun insert(book: Book) {
        bookDao.insert(book)
    }

    fun getBookById(id: Int): LiveData<Book> {
        return bookDao.getBookById(id)
    }

    suspend fun delete(book: Book) {
        bookDao.delete(book)
    }

    suspend fun update(book: Book) {
        bookDao.update(book)
    }
}