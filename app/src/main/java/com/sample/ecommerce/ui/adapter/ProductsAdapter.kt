package com.sample.ecommerce.ui.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.sample.ecommerce.R
import com.sample.ecommerce.data.api.ApiUrls
import com.sample.ecommerce.data.api.model.Product
import com.sample.ecommerce.databinding.ItemProductsBinding
import com.sample.ecommerce.utils.currencyFormat

class ProductsAdapter(
    private val clickListener: (Product) -> Unit
) : RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {

    private lateinit var products: ArrayList<Product>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position], clickListener)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    fun setData(products: ArrayList<Product>) {
        this.products = products
        notifyDataSetChanged()
    }

    class ProductViewHolder(private val binding: ItemProductsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Product, clickListener: (Product) -> Unit) {

            var imageUrl = ""
            when {
                item.image.isNotEmpty() -> imageUrl = item.image[0].replace("localhost", ApiUrls.IP)
            }
            with(binding) {
                product = item
                discountView.setDiscount(item.discountPercent != 0)
                discountView.setDiscountNumber("-${item.discountPercent}%")

                val discountAmount = (item.price * item.discountPercent) / 100
                val discountedPrice = item.price - discountAmount

                when {
                    item.discountPercent != 0 -> {
                        tvOriginalPrice.text = currencyFormat(item.price)
                        tvOriginalPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    }
                }
                tvPrice.text = currencyFormat(discountedPrice)

                ivProduct.load(imageUrl) {
                    placeholder(R.drawable.ic_baseline_image_24)
                    error(R.drawable.ic_baseline_image_24)
                }

                root.setOnClickListener {
                    clickListener(item)
                }
                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup): ProductViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemProductsBinding.inflate(layoutInflater, parent, false)
                return ProductViewHolder(binding)
            }
        }
    }

}