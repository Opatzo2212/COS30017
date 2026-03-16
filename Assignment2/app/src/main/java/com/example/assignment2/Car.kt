package com.example.assignment2

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Car(
    val id: Int,
    val name: String,
    val model: String,
    val year: Int,
    var rating: Float,
    val kilometers: Int,
    val dailyCost: Int,
    val imageResource: Int,
    var isFavorite: Boolean = false
) : Parcelable