package com.sample.ecommerce.data.api.model

data class Uploads(
    val images: List<String>
)

val EmptyUploads by lazy {
    Uploads(emptyList())
}