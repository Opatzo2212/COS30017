package com.example.assignment3

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = BookDatabase.getDatabase(application).userDao()

    fun registerUser(user: User, onResult: (Boolean, String) -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        val existingUser = userDao.getUserByEmail(user.email)
        if (existingUser != null) {
            launch(Dispatchers.Main) { onResult(false, "Email already exists") }
        } else {
            userDao.insert(user)
            launch(Dispatchers.Main) { onResult(true, "Account created successfully") }
        }
    }

    fun loginUser(email: String, password: String, onResult: (User?) -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        val user = userDao.login(email, password)
        launch(Dispatchers.Main) {
            onResult(user)
        }
    }
}