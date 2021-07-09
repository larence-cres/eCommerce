package com.sample.ecommerce.ui.fragment.detail

data class CartParams(
    val price: Int,
    val name: String,
    val size: String,
    val image: String,
    val color: String,
    val category: String,
    val quantity: Int,
    val productId: String,
)