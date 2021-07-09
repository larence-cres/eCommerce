package com.sample.ecommerce.ui.fragment.addProduct

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.sample.ecommerce.data.AppConstants
import com.sample.ecommerce.data.DataStorePreference
import com.sample.ecommerce.data.NetworkController
import com.sample.ecommerce.data.api.ApiUrls
import com.sample.ecommerce.data.api.model.Product
import com.sample.ecommerce.data.api.model.Products
import com.sample.ecommerce.data.api.model.Resource
import com.sample.ecommerce.data.api.model.Uploads
import com.sample.ecommerce.repository.MainRepository
import com.sample.ecommerce.utils.getErrorMessage
import io.ktor.client.features.*
import io.ktor.client.statement.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.*
import timber.log.Timber
import java.io.IOException

class AddProductViewModel(
    private val repository: MainRepository,
    private val networkController: NetworkController,
    private val preference: DataStorePreference,
) : ViewModel() {

    suspend fun categories(): List<String> = Gson().fromJson(
        preference.fetchString(AppConstants.PRODUCT_DATA).first(),
        Products::class.java
    ).categories

    private val _uploadImagesStateFlow: MutableStateFlow<Resource<Uploads>> =
        MutableStateFlow(Resource.empty(null))
    val uploadImagesStateFlow: StateFlow<Resource<Uploads>> = _uploadImagesStateFlow

    private val _productAddStateFlow: MutableStateFlow<Resource<Product>> =
        MutableStateFlow(Resource.empty(null))
    val productAddStateFlow: StateFlow<Resource<Product>> = _productAddStateFlow

    /**
     * Upload images
     * TODO :: Implement ktor multipart - Remove OkHttp call.enqueue()
     * @param body
     */
    fun uploadImages(body: RequestBody) =
        viewModelScope.launch {
            when {
                networkController.isConnected() -> {
                    _uploadImagesStateFlow.value = Resource.loading(null)
                    val request: Request = Request.Builder()
                        .url("http://${ApiUrls.BASE_URL}/${ApiUrls.UPLOAD}")
                        .post(body)
                        .build()

                    val client: OkHttpClient = OkHttpClient.Builder().build()
                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            Timber.e("Exception : $e")
                            _uploadImagesStateFlow.value =
                                Resource.error(
                                    null,
                                    e.message.toString(),
                                    402
                                )
                        }

                        override fun onResponse(call: Call, response: Response) {
                            _uploadImagesStateFlow.value = Resource.success(
                                Gson().fromJson(response.body?.string(), Uploads::class.java)
                            )
                        }
                    })
                }
                else -> {
                    _uploadImagesStateFlow.value = Resource.error(null, "No Internet Connection", 0)
                }
            }
        }

    fun uploadProduct(productParams: ProductParams, update: Boolean, id: String) {
        viewModelScope.launch {
            when {
                networkController.isConnected() -> {
                    _productAddStateFlow.value = Resource.loading(null)
                    try {
                        val response = when {
                            update -> repository.updateProduct(productParams, id)
                            else -> repository.uploadProduct(productParams)
                        }
                        _productAddStateFlow.value = Resource.success(response)
                    } catch (e: ResponseException) {
                        _productAddStateFlow.value =
                            Resource.error(
                                null,
                                getErrorMessage(e.response.readText()),
                                e.response.status.value
                            )
                    }
                }
                else -> {
                    _productAddStateFlow.value = Resource.error(null, "No Internet Connection", 0)
                }
            }
        }
    }
}