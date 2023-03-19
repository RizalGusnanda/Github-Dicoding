package com.dicoding.github

import android.app.usage.UsageEvents
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import com.dicoding.github.data.remote.ApiConfig
import com.dicoding.github.result.Result
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

class MainViewModel() : ViewModel() {

    val resultUser = MutableLiveData<Result>()

    fun getUser() {
        viewModelScope.launch {
            flow {
                val response = ApiConfig
                    .apiService
                    .getUserGithub()

                emit(response)
            }.onStart {
                resultUser.value = Result.Loading(true)
            }.onCompletion {
                resultUser.value = Result.Loading(false)
            }.catch {
                it.printStackTrace()
                resultUser.value = Result.Error(it)
            }.collect {
                resultUser.value = Result.Success(it)
            }
        }
    }

    fun getUser(username: String) {
        viewModelScope.launch {
            flow {
                val response = ApiConfig
                    .apiService
                    .searchUserGithub(
                        mapOf(
                            "q" to username,
                            "per_page" to 10
                        )
                    )

                emit(response)
            }.onStart {
                resultUser.value = Result.Loading(true)
            }.onCompletion {
                resultUser.value = Result.Loading(false)
            }.catch {
                it.printStackTrace()
                resultUser.value = Result.Error(it)
            }.collect {
                resultUser.value = Result.Success(it.items)
            }
        }
    }
}