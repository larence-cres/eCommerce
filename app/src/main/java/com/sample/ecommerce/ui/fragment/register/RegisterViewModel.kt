package com.sample.ecommerce.ui.fragment.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.sample.ecommerce.data.AppConstants
import com.sample.ecommerce.data.DataStorePreference
import com.sample.ecommerce.data.NetworkController
import com.sample.ecommerce.data.api.model.Auth
import com.sample.ecommerce.data.api.model.Resource
import com.sample.ecommerce.repository.MainRepository
import com.sample.ecommerce.ui.fragment.login.LoginParams
import com.sample.ecommerce.utils.getErrorMessage
import io.ktor.client.features.*
import io.ktor.client.statement.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repository: MainRepository,
    private val networkController: NetworkController,
    private val preference: DataStorePreference
) : ViewModel() {

    private val _authStateFlow: MutableStateFlow<Resource<Auth>> =
        MutableStateFlow(Resource.empty(null))
    val authStateFlow: StateFlow<Resource<Auth>> = _authStateFlow

    fun register(registerParams: RegisterParams) = viewModelScope.launch {
        when {
            networkController.isConnected() -> {
                _authStateFlow.value = Resource.loading(null)
                try {
                    val response = repository.register(registerParams)
                    _authStateFlow.value = Resource.success(response)
                } catch (e: ResponseException) {
                    _authStateFlow.value =
                        Resource.error(
                            null,
                            getErrorMessage(e.response.readText()),
                            e.response.status.value
                        )
                }
            }
            else -> {
                _authStateFlow.value = Resource.error(null, "No internet Connection", 0)
            }
        }
    }

    suspend fun saveAuthCredentials(data: Auth) {
        preference.saveString(AppConstants.ACCESS_TOKEN, data.token)
        preference.saveString(AppConstants.PROFILE_DATA, Gson().toJson(data.user))
    }

}