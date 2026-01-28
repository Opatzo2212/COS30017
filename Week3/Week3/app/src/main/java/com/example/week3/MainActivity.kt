package com.example.week3

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.jvm.java

class MainActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var Username: EditText
    lateinit var Password: EditText
    lateinit var btnLogin: Button
    lateinit var btnCancel: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        Username = findViewById(R.id.username)
        Password = findViewById(R.id.password)
        btnLogin = findViewById(R.id.login)
        btnCancel = findViewById(R.id.cancel)

        btnLogin.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        var usr = Username.text.toString()
        var pwd = Password.text.toString()
        when(v?.id){
            R.id.login -> (
                    if (usr == "Swin" && pwd == "123"){
                        Toast.makeText(this, "Login successful", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, testActivity::class.java)
                        intent.putExtra("Username", usr)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Wrong username or password", Toast.LENGTH_LONG).show()
                    }
            )
        }
    }
}