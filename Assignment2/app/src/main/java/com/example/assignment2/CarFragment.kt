package com.example.assignment2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class CarFragment : Fragment() {

    private var car: Car? = null
    private var isRented: Boolean = false
    private var rentalDuration: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            car = it.getParcelable("car_data")
            isRented = it.getBoolean("is_rented", false)
            rentalDuration = it.getInt("rental_duration", 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_car, container, false)

        val tvCarName = view.findViewById<TextView>(R.id.tvCarName)
        val btnFavorite = view.findViewById<ImageButton>(R.id.btnFavorite)
        val tvRentedOverlay = view.findViewById<TextView>(R.id.tvRentedOverlay)
        val ivCarImage = view.findViewById<ImageView>(R.id.ivCarImage)
        val rbCarRating = view.findViewById<RatingBar>(R.id.rbCarRating)
        val tvCarStats = view.findViewById<TextView>(R.id.tvCarStats)
        val tvRentalCost = view.findViewById<TextView>(R.id.tvRentalCost)

        car?.let { currentCar ->
            tvCarName.text = currentCar.name

            val heartResId = if (currentCar.isFavorite) R.drawable.heart_on else R.drawable.heart_off
            btnFavorite.setImageResource(heartResId)

            btnFavorite.setOnClickListener {
                currentCar.isFavorite = !currentCar.isFavorite
                val newIcon = if (currentCar.isFavorite) R.drawable.heart_on else R.drawable.heart_off
                btnFavorite.setImageResource(newIcon)
                Toast.makeText(context, if (currentCar.isFavorite) "Favorited" else "Unfavorited", Toast.LENGTH_SHORT).show()
            }

            if (isRented) {
                tvRentedOverlay.visibility = View.VISIBLE
                tvRentalCost.text = "Rented for: $rentalDuration Days"
                tvRentalCost.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
            } else {
                tvRentedOverlay.visibility = View.GONE
                tvRentalCost.text = "${currentCar.dailyCost} Credits / Day"
                tvRentalCost.setTextColor(resources.getColor(R.color.discord_blurple, null)) // Or your primary color
            }

            rbCarRating.rating = currentCar.rating
            tvCarStats.text = "${currentCar.model} (${currentCar.year}) \n${currentCar.kilometers} km total"
            ivCarImage.setImageResource(currentCar.imageResource)
        }

        return view
    }

    companion object {
        fun newInstance(car: Car, isRented: Boolean, duration: Int = 0): CarFragment {
            val fragment = CarFragment()
            val args = Bundle()
            args.putParcelable("car_data", car)
            args.putBoolean("is_rented", isRented)
            args.putInt("rental_duration", duration)
            fragment.arguments = args
            return fragment
        }
    }
}