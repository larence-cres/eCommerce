package com.sample.ecommerce.ui.fragment.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.ecommerce.data.DataStorePreference
import com.sample.ecommerce.data.NetworkController
import com.sample.ecommerce.data.api.model.Cart
import com.sample.ecommerce.data.api.model.Product
import com.sample.ecommerce.data.api.model.Resource
import com.sample.ecommerce.repository.MainRepository
import com.sample.ecommerce.utils.getErrorMessage
import io.ktor.client.features.*
import io.ktor.client.statement.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: MainRepository,
    private val networkController: NetworkController,
    private val preference: DataStorePreference,
) : ViewModel() {

    private val _productStateFlow: MutableStateFlow<Resource<Product>> =
        MutableStateFlow(Resource.empty(null))
    val productStateFlow: StateFlow<Resource<Product>> = _productStateFlow

    private val _cartStateFlow: MutableStateFlow<Resource<Cart>> =
        MutableStateFlow(Resource.empty(null))
    val cartStateFlow: StateFlow<Resource<Cart>> = _cartStateFlow

    fun getProductDetail(id: String) = viewModelScope.launch {
        when {
            networkController.isConnected() -> {
                _productStateFlow.value = Resource.loading(null)
                try {
                    val response = repository.getProductDetail(id)
                    _productStateFlow.value = Resource.success(response)
                } catch (e: ResponseException) {
                    _productStateFlow.value =
                        Resource.error(
                            null,
                            getErrorMessage(e.response.readText()),
                            e.response.status.value
                        )
                }
            }
            else -> {
                _productStateFlow.value = Resource.error(null, "No internet Connection", 0)
            }
        }
    }

    fun addToCart(cartParams: CartParams) = viewModelScope.launch {
        when {
            networkController.isConnected() -> {
                _cartStateFlow.value = Resource.loading(null)
                try {
                    val response = repository.addToCart(cartParams)
                    _cartStateFlow.value = Resource.success(response)
                } catch (e: ResponseException) {
                    _cartStateFlow.value =
                        Resource.error(
                            null,
                            getErrorMessage(e.response.readText()),
                            e.response.status.value
                        )
                }
            }
            else -> {
                _cartStateFlow.value = Resource.error(null, "No internet Connection", 0)
            }
        }
    }

}