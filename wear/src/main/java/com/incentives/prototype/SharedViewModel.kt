package com.incentives.prototype

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _spokenQueryUpdate = MutableLiveData<Event<String>>()
    val spokenQueryUpdate: LiveData<Event<String>> get() = _spokenQueryUpdate

    fun updateSpokenQuery(query: String) {
        _spokenQueryUpdate.value = Event(query)
    }
}

class Event<out T>(private val content: T) {
    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (!hasBeenHandled) {
            hasBeenHandled = true
            content
        } else {
            null
        }
    }

    fun peekContent(): T = content
}