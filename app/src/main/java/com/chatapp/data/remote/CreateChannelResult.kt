package com.chatapp.data.remote

sealed class CreateChannelResult {
    data class Error(val error: String): CreateChannelResult()
    object Success: CreateChannelResult()
}