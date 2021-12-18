package com.chatapp.data.remote

import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryUsersRequest
import io.getstream.chat.android.client.call.Call
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.ConnectionData
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.client.models.User
import java.util.*
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val chatClient: ChatClient
) {

    fun connectUser(user: User): Call<ConnectionData> {
        val token = chatClient.devToken(user.id)

        return chatClient.connectUser(
            user = user,
            token = token
        )
    }

    fun logout() {
        chatClient.disconnect()
    }

    fun getUser(): User? = chatClient.getCurrentUser()

    fun getUserId(): String? = chatClient.getCurrentUser()?.id

    fun createChannel(channelName: String): Call<Channel> {
        val trimName = channelName.trim()
        return chatClient.channel(
            channelType = "messaging",
            channelId = UUID.randomUUID().toString(),
        ).create(
            mapOf(
                "name" to trimName
            )
        )
    }

    fun createNewChannel(id: String): Call<Channel> {
        return chatClient.createChannel(
            channelType = "messaging",
            members = listOf(chatClient.getCurrentUser()!!.id, id),
            mapOf(
                "name" to "$id and ${chatClient.getCurrentUser()!!.id}"
            )
        )
    }

    fun queryAllUsers(): Call<List<User>> {
        val request = QueryUsersRequest(
            filter = Filters.and(
                Filters.ne("id", chatClient.getCurrentUser()!!.id),
//                Filters.ne("id", "dsafsdfdsfsdgdsg")
            ),
            offset = 0,
            limit = 100
        )
        return chatClient.queryUsers(request)
    }

    fun searchUser(query:String): Call<List<User>> {
        val filters = Filters.and(
            Filters.autocomplete("id", query),
            Filters.ne("id", chatClient.getCurrentUser()!!.id)
        )

        val request = QueryUsersRequest(
            filter = filters,
            offset = 0,
            limit = 100
        )

        return chatClient.queryUsers(request)
    }

}
