package com.sample.ecommerce.ui.fragment.register

data class RegisterParams(
    val username: String,
    val email: String,
    val address: String,
    val password: String,
    val seller: Boolean
)