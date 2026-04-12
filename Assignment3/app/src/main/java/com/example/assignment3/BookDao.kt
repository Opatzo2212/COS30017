package com.example.assignment3

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update

@Dao
interface BookDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(book: Book)

    @Query("SELECT * FROM books WHERE userId = :userId ORDER BY id DESC")
    fun getAllBooks(userId: Int): LiveData<List<Book>>

    @Query("SELECT * FROM books WHERE id = :id")
    fun getBookById(id: Int): LiveData<Book>

    @Delete
    suspend fun delete(book: Book)

    @Update
    suspend fun update(book: Book)
}