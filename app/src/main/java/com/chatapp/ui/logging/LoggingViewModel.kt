package com.chatapp.ui.logging

import androidx.lifecycle.ViewModel
import com.chatapp.data.remote.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class LoggingViewModel @Inject constructor(
    private val repo: ChatRepository
): ViewModel() {
}