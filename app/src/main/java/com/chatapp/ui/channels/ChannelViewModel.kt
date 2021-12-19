package com.chatapp.ui.channels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatapp.data.remote.ChatRepository
import com.chatapp.data.remote.CreateChannelResult
import com.chatapp.preferences.PreferenceStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.call.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChannelViewModel @Inject constructor(
    private val repo: ChatRepository,
    private val preferences: PreferenceStorage
    ): ViewModel() {

    private val _createChannelEvent = MutableSharedFlow<CreateChannelResult>()
    val createChannelEvent = _createChannelEvent.asSharedFlow()

    private val _isRemembered = MutableSharedFlow<Boolean>()
    val isRemembered = _isRemembered.asSharedFlow()

    fun forgetUser() {
        preferences.isRemembered = false
        preferences.userName = ""
    }

    fun logout() {
        repo.logout()
    }

    fun getCurrentUser() =
        repo.getUser()

}