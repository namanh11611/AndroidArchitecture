package com.henry.androidarchitecture.data.repository

sealed class ResultState<T> {
    data class Success<T>(val data: T) : ResultState<T>()
    data class Error<T>(val message: String?): ResultState<T>()
    object Loading: ResultState<Nothing>()

    fun getCurrentData(): T? {
        return when (this) {
            is Success -> this.data
            is Error, Loading -> null
        }
    }

    override fun toString(): String {
        return when (this) {
            is Success -> "Success[data=$]"
            is Error -> "Error[message=$message]"
            is Loading -> "Loading"
        }
    }
}
