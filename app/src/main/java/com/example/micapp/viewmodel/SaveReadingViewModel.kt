package com.example.micapp.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SaveReadingViewModel : ViewModel() {

    private val _lastReading = MutableLiveData<String>()
    val lastReading: LiveData<String> get() = _lastReading

    fun setLastReading(reading: String) {
        _lastReading.value = reading
    }
}
