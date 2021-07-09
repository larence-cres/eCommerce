package com.sample.ecommerce.data.api.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class Products(
    val products: Map<String, List<Product>>,
    val categories: List<String>
//    @SerializedName("top_picks") val topPicks: List<TopPicks>,
)

val EmptyProducts by lazy {
    Products(emptyMap(), emptyList())
}

data class TopPicks(
    val id: String,
    val name: String,
    val image: List<String>
)

val EmptyTopPicks by lazy {
    TopPicks("", "", emptyList())
}

@Parcelize
data class Product(
    val price: Int,
    val id: String,
    val name: String,
    val created: String,
    val ownerId: String,
    val category: String,
    val description: String,

    val size: List<String>,
    val image: List<String>,
    val color: List<String>,

    val soldQuantity: Int,
    val orderQuantity: Int,
    val discountPercent: Int,
    val availableQuantity: Int,
) : Parcelable

val EmptyProduct by lazy {
    Product(0, "", "", "", "", "", "", emptyList(), emptyList(), emptyList(), 0, 0, 0, 0)
}

data class FilteredProducts(
    val products: List<Product>
)

val EmptyProductsByType by lazy {
    FilteredProducts(emptyList())
}