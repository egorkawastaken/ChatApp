package com.chatapp.util

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.livedata.ChatDomain
import javax.inject.Inject

@HiltAndroidApp
class ChatApp: Application() {

    @Inject
    private lateinit var client: ChatClient
    /**
     * StreamSDK initialization
     * @param client is used in all project. Makes all network requests for StreamSDK servers
     * */

    override fun onCreate() {
        super.onCreate()



        ChatDomain.Builder(client,applicationContext).build()
    }
}