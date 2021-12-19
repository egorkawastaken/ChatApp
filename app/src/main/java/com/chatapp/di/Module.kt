package com.chatapp.di

import android.content.Context
import com.chatapp.preferences.PreferenceStorage
import com.chatapp.preferences.SharedPreferencesStorage
import com.example.chatapp.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class Module {

    @Provides
    @Singleton
    fun provideStreamChatClient(@ApplicationContext context: Context)
        = ChatClient.Builder(context.getString(R.string.api_key),context).logLevel(ChatLogLevel.ALL).build()

    @Provides
    @Singleton
    fun providePreferenceStorage(@ApplicationContext context: Context): PreferenceStorage = SharedPreferencesStorage(context)

}