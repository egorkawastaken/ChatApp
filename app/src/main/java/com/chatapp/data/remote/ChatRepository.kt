package com.chatapp.data.remote

import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.call.Call
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.ConnectionData
import io.getstream.chat.android.client.models.User
import java.util.*
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

    fun logout() {
        chatClient.disconnect()
    }

    fun getUser(): User? = chatClient.getCurrentUser()

    fun createChannel(channelName: String): Call<Channel> {
        val trimName = channelName.trim()
        return chatClient.channel(
            channelType = "messaging",
            channelId = UUID.randomUUID().toString()
        ).create(
            mapOf(
                "name" to trimName
            )
        )
    }

}
