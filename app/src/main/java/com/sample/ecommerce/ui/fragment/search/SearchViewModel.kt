package com.sample.ecommerce.ui.fragment.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.sample.ecommerce.data.AppConstants
import com.sample.ecommerce.data.DataStorePreference
import com.sample.ecommerce.data.NetworkController
import com.sample.ecommerce.data.api.model.FilteredProducts
import com.sample.ecommerce.data.api.model.Resource
import com.sample.ecommerce.data.api.model.User
import com.sample.ecommerce.repository.MainRepository
import com.sample.ecommerce.utils.getErrorMessage
import io.ktor.client.features.*
import io.ktor.client.statement.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: MainRepository,
    private val networkController: NetworkController,
    private val preference: DataStorePreference,
) : ViewModel() {

    private val _productsStateFlow: MutableStateFlow<Resource<FilteredProducts>> =
        MutableStateFlow(Resource.empty(null))
    val productsStateFlow: StateFlow<Resource<FilteredProducts>> = _productsStateFlow

    fun searchProducts(searchText: String) = viewModelScope.launch {
        when {
            networkController.isConnected() -> {
                _productsStateFlow.value = Resource.loading(null)
                try {
                    val response = repository.searchProducts(searchText)
                    _productsStateFlow.value = Resource.success(response)
                } catch (e: ResponseException) {
                    _productsStateFlow.value =
                        Resource.error(
                            null,
                            getErrorMessage(e.response.readText()),
                            e.response.status.value
                        )
                }
            }
            else -> {
                _productsStateFlow.value = Resource.error(null, "No internet Connection", 0)
            }
        }
    }

    val user = preference.fetchString(AppConstants.PROFILE_DATA)
    fun user(user: String): User = Gson().fromJson(user, User::class.java)

}