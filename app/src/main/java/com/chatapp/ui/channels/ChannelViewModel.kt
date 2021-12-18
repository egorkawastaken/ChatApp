package com.chatapp.ui.channels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatapp.data.remote.ChatRepository
import com.chatapp.data.remote.CreateChannelResult
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.call.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChannelViewModel @Inject constructor(
    private val repo: ChatRepository
): ViewModel() {

    private val _createChannelEvent = MutableSharedFlow<CreateChannelResult>()
    val createChannelEvent = _createChannelEvent.asSharedFlow()

    fun logout() {
        repo.logout()
    }

    fun getCurrentUser() =
        repo.getUser()


    fun getCurrentUserId() {
        repo.getUserId()
    }

    fun createChannel(channelName: String) {

       viewModelScope.launch(Dispatchers.IO) {
           if (channelName.isEmpty()) {
               _createChannelEvent.emit(CreateChannelResult.Error( "The channel name has to be filled."))
           }

           val result = repo.createChannel(channelName).await()
           if (result.isError) {
               _createChannelEvent.emit(CreateChannelResult.Error(result.error().message ?: "Unknown Error"))
           } else if(result.isSuccess) {
               _createChannelEvent.emit(CreateChannelResult.Success)
           }
        }
    }

}