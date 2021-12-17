package com.chatapp.data.remote

import com.chatapp.util.Constants
import io.getstream.chat.android.client.ChatClient
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val chatClient: ChatClient
) {

    suspend fun connectUser(userName: String) {
        chatClient.connectGuestUser(
            userId = userName,
            username = userName
        )
    }

    private fun isUsernameValid(userName: String): Boolean = (userName.length >= Constants.MINIMUM_USERNAME_LENGTH)
}
