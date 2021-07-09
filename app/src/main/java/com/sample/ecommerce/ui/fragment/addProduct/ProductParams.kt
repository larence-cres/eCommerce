package com.sample.ecommerce.ui.fragment.addProduct

data class ProductParams(
    val name: String,
    val size: List<String>,
    val image: List<String>,
    val color: List<String>,
    val price: Int,
    val category: String,
    val description: String,
    val discountPercent: Int,
    val availableQuantity: Int,
)