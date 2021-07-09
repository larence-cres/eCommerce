package com.sample.ecommerce.repository

import com.sample.ecommerce.data.api.ApiService
import com.sample.ecommerce.ui.fragment.addProduct.ProductParams
import com.sample.ecommerce.ui.fragment.detail.CartParams
import com.sample.ecommerce.ui.fragment.login.LoginParams
import com.sample.ecommerce.ui.fragment.register.RegisterParams
import io.ktor.util.*
import okhttp3.RequestBody
import java.io.File

class MainRepository(private val apiService: ApiService) {

    suspend fun getAllProducts() = apiService.getAllProducts()

    suspend fun getProductDetail(id: String) = apiService.getProductDetail(id)

    suspend fun getProductsByCategory(category: String) = apiService.getProductsByCategory(category)

    suspend fun searchProducts(searchText: String) = apiService.searchProducts(searchText)

    suspend fun login(loginParams: LoginParams) = apiService.login(loginParams)

    suspend fun register(registerParams: RegisterParams) = apiService.register(registerParams)

    suspend fun uploadProduct(productParams: ProductParams) = apiService.uploadProduct(productParams)

    suspend fun updateProduct(productParams: ProductParams, id: String) = apiService.updateProduct(productParams, id)

    suspend fun addToCart(cartParams: CartParams) = apiService.addToCart(cartParams)

}