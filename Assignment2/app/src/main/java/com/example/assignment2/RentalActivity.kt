package com.example.assignment2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.slider.Slider

class RentalActivity : AppCompatActivity() {

    private var selectedCar: Car? = null
    private var currentBalance = 0
    private var daysRented = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rental)

        selectedCar = intent.getParcelableExtra("car_data")
        currentBalance = intent.getIntExtra("current_balance", 0)

        val ivImage = findViewById<ImageView>(R.id.ivRentalCarImage)
        val tvName = findViewById<TextView>(R.id.tvRentalCarName)
        val tvModelYear = findViewById<TextView>(R.id.tvRentalCarModelYear)
        val rbRating = findViewById<RatingBar>(R.id.rbRentalCarRating)
        val tvStats = findViewById<TextView>(R.id.tvRentalCarStats)
        val tvDailyCost = findViewById<TextView>(R.id.tvRentalDailyCost)
        val tvTotalCost = findViewById<TextView>(R.id.tvTotalRentalCost)
        val sliderDays = findViewById<Slider>(R.id.sliderDays)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnCancel = findViewById<Button>(R.id.btnCancel)

        selectedCar?.let { car ->
            ivImage.setImageResource(car.imageResource)
            tvName.text = car.name
            tvModelYear.text = "${car.model} (${car.year})"
            rbRating.rating = car.rating
            tvStats.text = "Kilometers: ${car.kilometers} km"
            tvDailyCost.text = "Daily Rate: ${car.dailyCost} Credits"

            updateTotalCostDisplay(tvTotalCost, car.dailyCost)
        }

        sliderDays.addOnChangeListener { _, value, _ ->
            daysRented = value.toInt()
            selectedCar?.let { car ->
                updateTotalCostDisplay(tvTotalCost, car.dailyCost)
            }
        }

        btnSave.setOnClickListener {
            confirmRental()
        }

        btnCancel.setOnClickListener {
            cancelRental()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                cancelRental()
            }
        })
    }

    private fun updateTotalCostDisplay(textView: TextView, dailyRate: Int) {
        val total = dailyRate * daysRented
        textView.text = getString(R.string.rental_total_cost, total, daysRented)
    }

    private fun confirmRental() {
        selectedCar?.let { car ->
            val totalCost = car.dailyCost * daysRented

            if (totalCost > 400) {
                Toast.makeText(this, getString(R.string.rental_cost_limit_exceeded, totalCost), Toast.LENGTH_LONG).show()
                return
            }

            if (totalCost > currentBalance) {
                Toast.makeText(this, getString(R.string.insufficient_balance, totalCost, currentBalance), Toast.LENGTH_LONG).show()
                return
            }

            val resultIntent = Intent()
            resultIntent.putExtra("rented_car", car)
            resultIntent.putExtra("total_cost", totalCost)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun cancelRental() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}