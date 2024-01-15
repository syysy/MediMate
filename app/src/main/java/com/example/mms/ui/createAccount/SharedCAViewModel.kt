package com.example.mms.ui.createAccount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mms.model.User

class SharedCAViewModel : ViewModel() {
    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> get() = _userData

    fun setUserData(user: User) {
        _userData.value = user
    }
}