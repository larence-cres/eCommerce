package com.sample.ecommerce.data.api.model

import com.google.gson.annotations.SerializedName

data class Auth(
    val user: User,
    val token: String
)

val EmptyAuth by lazy {
    Auth(EmptyUser, "")
}

data class User(
    @SerializedName("_id") val id: String,
    
    val email: String,
    val avatar: String,
    val created: String,
    val address: String,
    val seller: Boolean,
    val username: String,
)

val EmptyUser by lazy {
    User("", "", "", "", "", false, "")
}
