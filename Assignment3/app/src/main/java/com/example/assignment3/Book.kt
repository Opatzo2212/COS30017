package com.example.assignment3

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val title: String,
    val author: String,
    val tags: String,
    val link: String,
    val description: String,
    val coverImageUri: String?,
    val status: String = "Plan to Read",
    val currentChapter: Int = 0
)