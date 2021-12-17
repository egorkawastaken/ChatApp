package com.chatapp.data.remote

sealed class LoggingResult {
    object ErrorInputTooShort: LoggingResult()
    data class Error(val message: String): LoggingResult()
    data class Success(val name: String): LoggingResult()
}