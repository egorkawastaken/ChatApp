package com.chatapp.data.remote

import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.call.Call
import io.getstream.chat.android.client.models.ConnectionData
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val chatClient: ChatClient
) {

    fun connectUser(userName: String): Call<ConnectionData> {
        return chatClient.connectGuestUser(
            userId = userName,
            username = userName
        )
    }

}
