package com.example.assignment2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import android.widget.SearchView
import android.widget.PopupMenu
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var btnNext: Button
    private lateinit var btnRent: Button
    private lateinit var tvCreditBalance: TextView
    private lateinit var tvLastRented: TextView
    private lateinit var btnDarkModeToggle: ImageButton
    private lateinit var btnPrev: Button
    private lateinit var tabLayout: TabLayout
    private lateinit var searchCars: SearchView
    private lateinit var btnSort: ImageButton

    private var availableCars = mutableListOf<Car>()
    private var rentedCars = mutableListOf<Car>()
    private var rentalDurations = mutableMapOf<Int, Int>()

    private var currentCarIndex = 0
    private var currentTabIndex = 0
    private var creditBalance = 500
    private var searchQuery = ""
    private var currentSortMode = "Default"

    private val rentalResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val rentedCar = result.data?.getParcelableExtra<Car>("rented_car")
            val totalCost = result.data?.getIntExtra("total_cost", 0) ?: 0
            val days = result.data?.getIntExtra("days_rented", 1) ?: 1

            rentedCar?.let { car ->
                creditBalance -= totalCost
                updateCreditDisplay()

                tvLastRented.visibility = View.VISIBLE
                tvLastRented.text = "Last Rented: ${car.name} for $days days"

                availableCars.removeAll { it.id == car.id }
                rentedCars.add(car)
                rentalDurations[car.id] = days

                Toast.makeText(this, "Rental Confirmed!", Toast.LENGTH_SHORT).show()
                currentCarIndex = 0
                showCarFragment()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeCars()
        setupViews()
        setupTabs()
        setupSearch()

        updateCreditDisplay()
        showCarFragment()
    }

    private fun setupViews() {
        btnPrev = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)
        btnRent = findViewById(R.id.btnRent)
        tvCreditBalance = findViewById(R.id.tvCreditBalance)
        tvLastRented = findViewById(R.id.tvLastRented)
        btnDarkModeToggle = findViewById(R.id.btnDarkModeToggle)
        tabLayout = findViewById(R.id.tabLayout)
        searchCars = findViewById(R.id.searchCars)
        btnSort = findViewById(R.id.btnSort)

        btnPrev.setOnClickListener(this)
        btnNext.setOnClickListener(this)
        btnRent.setOnClickListener(this)
        btnSort.setOnClickListener { showSortMenu(it) }

        val isDark = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        btnDarkModeToggle.setImageResource(if (isDark) R.drawable.ic_moon else R.drawable.ic_sun)

        btnDarkModeToggle.setOnClickListener {
            val isCurrentlyDark = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
            if (isCurrentlyDark) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                btnDarkModeToggle.setImageResource(R.drawable.ic_sun)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                btnDarkModeToggle.setImageResource(R.drawable.ic_moon)
            }
        }
    }

    private fun setupSearch() {
        searchCars.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                searchQuery = newText.orEmpty()
                currentCarIndex = 0
                showCarFragment()
                return true
            }
        })
    }

    private fun showSortMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menu.add("Rating (High to Low)")
        popup.menu.add("Year (Newest to Oldest)")
        popup.menu.add("Cost (Low to High)")
        popup.menu.add("Default")

        popup.setOnMenuItemClickListener { item ->
            currentSortMode = item.title.toString()
            currentCarIndex = 0
            showCarFragment()
            true
        }
        popup.show()
    }

    private fun getCurrentList(): List<Car> {
        var list = when (currentTabIndex) {
            0 -> availableCars
            1 -> (availableCars + rentedCars).filter { it.isFavorite }
            2 -> rentedCars
            else -> availableCars
        }.toMutableList()

        if (searchQuery.isNotEmpty()) {
            list = list.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.model.contains(searchQuery, ignoreCase = true)
            }.toMutableList()
        }

        return when (currentSortMode) {
            "Rating (High to Low)" -> list.sortedByDescending { it.rating }
            "Year (Newest to Oldest)" -> list.sortedByDescending { it.year }
            "Cost (Low to High)" -> list.sortedBy { it.dailyCost }
            else -> list
        }
    }

    private fun showCarFragment() {
        val list = getCurrentList()
        if (list.isNotEmpty()) {
            val car = list[currentCarIndex]
            val isRented = rentedCars.any { it.id == car.id }
            val duration = rentalDurations[car.id] ?: 0

            val fragment = CarFragment.newInstance(car, isRented, duration)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, androidx.fragment.app.Fragment())
                .commit()
        }
    }

    private fun initializeCars() {
        availableCars.add(Car(1, "Toyota", "Camry", 2024, 4.5f, 5000, 45, R.drawable.car1))
        availableCars.add(Car(2, "Honda", "Civic", 2023, 4.2f, 12000, 40, R.drawable.car2))
        availableCars.add(Car(3, "Ford", "Mustang", 2024, 4.8f, 2000, 80, R.drawable.car3))
        availableCars.add(Car(4, "Tesla", "Model 3", 2024, 4.7f, 8000, 60, R.drawable.car4))
        availableCars.add(Car(5, "BMW", "X5", 2023, 4.6f, 15000, 70, R.drawable.car5))
    }

    override fun onClick(v: View?) {
        val list = getCurrentList()
        when (v?.id) {
            R.id.btnPrev -> {
                if (list.isNotEmpty()) {
                    currentCarIndex = if (currentCarIndex <= 0) list.size - 1 else currentCarIndex - 1
                    showCarFragment()
                }
            }
            R.id.btnNext -> {
                if (list.isNotEmpty()) {
                    currentCarIndex = (currentCarIndex + 1) % list.size
                    showCarFragment()
                }
            }
            R.id.btnRent -> {
                if (currentTabIndex == 2) cancelRental() else startRentalProcess()
            }
        }
    }

    private fun cancelRental() {
        val list = getCurrentList()
        if (list.isNotEmpty() && currentTabIndex == 2) {
            val carToCancel = list[currentCarIndex]
            rentedCars.removeAll { it.id == carToCancel.id }
            rentalDurations.remove(carToCancel.id)
            availableCars.add(carToCancel)
            currentCarIndex = 0
            showCarFragment()
            Toast.makeText(this, "Rental Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startRentalProcess() {
        val list = getCurrentList()
        if (list.isEmpty()) return
        val selectedCar = list[currentCarIndex]
        if (rentedCars.any { it.id == selectedCar.id }) {
            Toast.makeText(this, "This car is already rented!", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, RentalActivity::class.java)
        intent.putExtra("car_data", selectedCar)
        intent.putExtra("current_balance", creditBalance)
        rentalResultLauncher.launch(intent)
    }

    private fun setupTabs() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTabIndex = tab?.position ?: 0
                currentCarIndex = 0
                updateUIForTab()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun updateUIForTab() {
        btnRent.text = if (currentTabIndex == 2) "Cancel" else "Rent"
        showCarFragment()
    }

    private fun updateCreditDisplay() {
        tvCreditBalance.text = "Credits: $creditBalance"
    }
}