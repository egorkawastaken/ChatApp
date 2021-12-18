package com.chatapp.ui.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatapp.data.remote.ChatRepository
import com.chatapp.data.remote.CreateChannelResult
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.call.await
import io.getstream.chat.android.client.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val repo: ChatRepository
): ViewModel() {

    private val _createChannelEvent = MutableSharedFlow<CreateChannelResult>()
    val createChannelEvent = _createChannelEvent.asSharedFlow()

    private val _createChannelId = MutableSharedFlow<String>()
    val createChannelId = _createChannelId.asSharedFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    private val _listOfUsers = MutableStateFlow<List<User>>(listOf())
    val listOfUsers = _listOfUsers.asStateFlow()

    fun createNewChannel(id: String) {
         viewModelScope.launch(Dispatchers.IO) {
            val result = repo.createNewChannel(id).await()
             if (result.isError) {
                 _createChannelEvent.emit(CreateChannelResult.Error(result.error().message ?: "Unknown Error"))
             } else if(result.isSuccess) {
                 _createChannelId.emit(result.data().cid)
                 _createChannelEvent.emit(CreateChannelResult.Success)
             }
        }
    }

    fun getAllUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repo.queryAllUsers().await()
            if (result.isError) {
                _errorMessage.emit(result.error().message ?: "Unknown Error")
            } else if(result.isSuccess) {
                _listOfUsers.emit(result.data())
            }
        }
    }

    fun searchUser(query:String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repo.searchUser(query).await()
            if (result.isError) {
                _errorMessage.emit(result.error().message ?: "Unknown Error")
            } else if(result.isSuccess) {
                _listOfUsers.emit(result.data())
            }
        }
    }

}