package com.example.mms.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mms.model.User

// Class used to share data between main fragments

class MainViewModel : ViewModel() {
    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> get() = _userData

    fun setUserData(user: User) {
        _userData.value = user
    }

    private val _medecinName = MutableLiveData<String>()
    val medecinName: LiveData<String> get() = _medecinName

    fun setMedecinName(name: String) {
        _medecinName.value = name
    }

    private val _medecinPhone = MutableLiveData<String>()
    val medecinPhone: LiveData<String> get() = _medecinPhone

    fun setMedecinPhone(phone: String) {
        _medecinPhone.value = phone
    }

    private val _medecinMail = MutableLiveData<String>()
    val medecinMail: LiveData<String> get() = _medecinMail

    fun setMedecinMail(mail: String) {
        _medecinMail.value = mail
    }

}