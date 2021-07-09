package com.sample.ecommerce.ui.fragment.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.sample.ecommerce.data.AppConstants
import com.sample.ecommerce.data.DataStorePreference
import com.sample.ecommerce.data.NetworkController
import com.sample.ecommerce.data.api.model.Product
import com.sample.ecommerce.data.api.model.Products
import com.sample.ecommerce.data.api.model.Resource
import com.sample.ecommerce.data.api.model.User
import com.sample.ecommerce.repository.MainRepository
import com.sample.ecommerce.utils.getErrorMessage
import io.ktor.client.features.*
import io.ktor.client.statement.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: MainRepository,
    private val networkController: NetworkController,
    private val preference: DataStorePreference
) : ViewModel() {

    private val _productsStateFlow: MutableStateFlow<Resource<Products>> =
        MutableStateFlow(Resource.empty(null))
    val productsStateFlow: StateFlow<Resource<Products>> = _productsStateFlow

    fun getAllProducts() = viewModelScope.launch {
        when {
            networkController.isConnected() -> {
                _productsStateFlow.value = Resource.loading(null)
                try {
                    val response = repository.getAllProducts()
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

    suspend fun saveProducts(products: Products?){
        preference.saveString(AppConstants.PRODUCT_DATA, Gson().toJson(products))
    }

    val products = preference.fetchString(AppConstants.PRODUCT_DATA)
    fun products(products: String): Products = Gson().fromJson(products, Products::class.java)

    val user = preference.fetchString(AppConstants.PROFILE_DATA)
    fun user(user: String): User = Gson().fromJson(user, User::class.java)

    val token = preference.fetchString(AppConstants.ACCESS_TOKEN)

}