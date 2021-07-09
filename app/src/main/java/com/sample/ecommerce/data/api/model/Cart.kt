package com.sample.ecommerce.data.api.model

data class Cart(
    val price: Int,
    val id: String,
    val name: String,
    val size: String,
    val image: String,
    val color: String,
    val quantity: Int,
    val ownerId: String,
    val created: String,
    val category: String,
    val productId: String,
)