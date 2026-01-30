package com.example.assignment_1

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var title: TextView
    lateinit var score: TextView
    lateinit var cur_el_num: TextView
    lateinit var btnPerform: Button
    lateinit var btnDeduction: Button
    lateinit var btnReset: Button
    lateinit var btnLangague: ImageButton

    private var currentScore = 0
    private var currentElement = 0
    private var hasTakenDeduction = false
    private var isVietnamese = false

    private var saveScore = "score"
    private var saveElement = "cur_elem"
    private var saveHaveDeduction = "has_taken_deduction"
    private var saveIsVietnamese = "is_vietnamese"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        title = findViewById(R.id.gymApp)
        score = findViewById(R.id.score)
        cur_el_num = findViewById(R.id.cur_el_num)
        btnReset = findViewById(R.id.reset)
        btnDeduction = findViewById(R.id.deduction)
        btnPerform = findViewById(R.id.perform)
        btnLangague = findViewById(R.id.languageOption)

        btnReset.setOnClickListener(this)
        btnPerform.setOnClickListener(this)
        btnDeduction.setOnClickListener(this)
        btnLangague.setOnClickListener(this)

        if (savedInstanceState != null) {
            currentScore = savedInstanceState.getInt(saveScore)
            currentElement = savedInstanceState.getInt(saveElement)
            hasTakenDeduction = savedInstanceState.getBoolean(saveHaveDeduction)
            isVietnamese = savedInstanceState.getBoolean(saveIsVietnamese)
        }

        updateUI()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(saveScore, currentScore)
        outState.putInt(saveElement, currentElement)
        outState.putBoolean(saveHaveDeduction, hasTakenDeduction)
        outState.putBoolean(saveIsVietnamese, isVietnamese)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.perform -> Perform()
            R.id.deduction -> Deduction()
            R.id.reset -> Reset()
            R.id.languageOption -> toggleLanguage()
        }
    }

    private fun toggleLanguage() {
        isVietnamese = !isVietnamese
        updateUI()
    }

    private fun Perform() {
        if (currentElement < 10 && !hasTakenDeduction) {
            currentElement++

            val points = when (currentElement) {
                in 1..3 -> 1
                in 4..7 -> 2
                in 8..10 -> 3
                else -> 0
            }

            currentScore += points
            Log.d("Debug", "Score changed")
            updateUI()
            if (currentElement == 10) {
                val completionMsg = if (isVietnamese) "Buổi tập đã hoàn thành!" else "Routine Complete!"
                Toast.makeText(this, completionMsg, Toast.LENGTH_LONG).show()
            }
        } else if (hasTakenDeduction) {
            Toast.makeText(this, "Cannot perform after deduction!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun Deduction() {
        if (currentElement in 1..9 && !hasTakenDeduction) {
            currentScore = (currentScore - 2).coerceAtLeast(0)
            hasTakenDeduction = true
            Log.d("Debug", "Score deducted")
            updateUI()
        } else if (currentElement == 10) {
            Toast.makeText(this, "Routine finished. No deductions allowed.", Toast.LENGTH_SHORT).show()
        } else if (currentElement == 0) {
            Toast.makeText(this, "Perform first element before deduction.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun Reset() {
        currentScore = 0
        currentElement = 0
        hasTakenDeduction = false
        Log.d("Debug", "Score reset")
        updateUI()
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        if (isVietnamese) {
            title.text = "Ứng dụng thể dục"
            score.text = "Điểm: $currentScore"
            cur_el_num.text = "Động tác: $currentElement"
            btnPerform.text = "Thực hiện"
            btnDeduction.text = "Trừ điểm"
            btnReset.text = "Đặt lại"
            btnLangague.setImageResource(R.drawable.vn_flag)
        } else {
            title.text = "Gymnastics App"
            score.text = "Score: $currentScore"
            cur_el_num.text = "Element: $currentElement"
            btnPerform.text = "Perform"
            btnDeduction.text = "Deduction"
            btnReset.text = "Reset"
            btnLangague.setImageResource(R.drawable.flag_en)
        }

        val zoneColor = when (currentElement) {
            in 1..3 -> Color.BLUE
            in 4..7 -> Color.GREEN
            in 8..10 -> "#FFA500".toColorInt()
            else -> Color.WHITE
        }
        score.setTextColor(zoneColor)

        btnPerform.isEnabled = (currentElement < 10 && !hasTakenDeduction)
        btnDeduction.isEnabled = (currentElement in 1..9 && !hasTakenDeduction)
    }
}