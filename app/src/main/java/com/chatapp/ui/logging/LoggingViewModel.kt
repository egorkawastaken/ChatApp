package com.chatapp.ui.logging

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.chatapp.data.remote.ChatRepository
import com.chatapp.data.remote.LoggingResult
import com.chatapp.preferences.PreferenceStorage
import com.chatapp.util.ChatApp
import com.chatapp.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.call.await
import io.getstream.chat.android.client.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoggingViewModel @Inject constructor(
    private val repo: ChatRepository,
    private val preferences: PreferenceStorage,
    app: Application
) : AndroidViewModel(app) {

    private val _isRemembered = MutableSharedFlow<Boolean>()
    val isRemembered = _isRemembered.asSharedFlow()

    private val _userName = MutableSharedFlow<String>()
    val userName = _userName.asSharedFlow()

    private val _loggingEvent = MutableSharedFlow<LoggingResult>()
    val loggingEvent = _loggingEvent.asSharedFlow()

    fun connectUser(userName: String) {
        val user = User(
            id = userName
        )

        viewModelScope.launch(Dispatchers.IO) {
            if (hasInternetConnection()) {
                if (isUsernameValid(userName)) {
                    val result = repo.connectUser(user).await()
                    if (result.isError) {
                        _loggingEvent.emit(
                            LoggingResult.Error(
                                result.error().message ?: "Unknown Error"
                            )
                        )
                        return@launch
                    }
                    if (result.isSuccess) {
                        _loggingEvent.emit(LoggingResult.Success(result.data().user.name))
                    }
                }
                else {
                    _loggingEvent.emit(LoggingResult.ErrorInputTooShort)
                }
            } else {
                _loggingEvent.emit(LoggingResult.Error("Check your internet connection."))
            }

        }
    }

    fun rememberUser(userName: String) {
       preferences.isRemembered = true
       preferences.userName = userName
    }

    fun isRemembered() {
        if (preferences.isRemembered) {
            viewModelScope.launch {
                _isRemembered.emit(true)
                val user = preferences.userName
                _userName.emit(user)
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<ChatApp>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    private fun isUsernameValid(userName: String): Boolean =
        (userName.length >= Constants.MINIMUM_USERNAME_LENGTH && userName.isNotEmpty())

}