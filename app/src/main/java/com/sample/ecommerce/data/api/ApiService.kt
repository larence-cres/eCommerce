package com.sample.ecommerce.data.api

import com.sample.ecommerce.data.api.model.*
import com.sample.ecommerce.ui.fragment.addProduct.ProductParams
import com.sample.ecommerce.ui.fragment.detail.CartParams
import com.sample.ecommerce.ui.fragment.login.LoginParams
import com.sample.ecommerce.ui.fragment.register.RegisterParams
import io.ktor.client.*
import io.ktor.client.request.*

class ApiService(private val client: HttpClient) {

    suspend fun getAllProducts(): Products {
        return client.get(ApiUrls.PRODUCT)
    }

    suspend fun getProductDetail(id: String): Product {
        return client.get("${ApiUrls.PRODUCT}/$id")
    }

    suspend fun getProductsByCategory(category: String): FilteredProducts {
        return client.get("${ApiUrls.PRODUCT}/${ApiUrls.CATEGORY}/$category")
    }

    suspend fun searchProducts(searchText: String): FilteredProducts {
        return client.get("${ApiUrls.PRODUCT}/${ApiUrls.SEARCH}/$searchText")
    }

    suspend fun uploadProduct(productParams: ProductParams): Product {
        return client.post(ApiUrls.PRODUCT) {
            body = productParams
        }
    }

    suspend fun updateProduct(productParams: ProductParams, id: String): Product {
        return client.put("${ApiUrls.PRODUCT}/$id") {
            body = productParams
        }
    }

    suspend fun addToCart(cartParams: CartParams): Cart {
        return client.post(ApiUrls.CART) {
            body = cartParams
        }
    }

    suspend fun login(loginParams: LoginParams): Auth {
        return client.post(ApiUrls.LOGIN) {
            body = loginParams
        }
    }

    suspend fun register(registerParams: RegisterParams): Auth {
        return client.post(ApiUrls.REGISTER) {
            body = registerParams
        }
    }

}