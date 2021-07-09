package com.sample.ecommerce.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sample.ecommerce.data.api.model.Product
import com.sample.ecommerce.databinding.ItemProductsListBinding
import com.sample.ecommerce.utils.currencyFormat

class ProductListAdapter(
    private val context: Context,
    private val clickListener: (Product) -> Unit,
) : RecyclerView.Adapter<ProductListAdapter.ProductListViewHolder>() {

    private lateinit var products: List<Product>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder {
        return ProductListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int) {
        holder.bind(products[position], context, clickListener)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    fun setData(products: List<Product>) {
        this.products = products
        notifyDataSetChanged()
    }

    class ProductListViewHolder(private val binding: ItemProductsListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: Product,
            context: Context,
            clickListener: (Product) -> Unit,
        ) {
            with(binding) {
                product = item
                tvPrice.text = "Rs. ${currencyFormat(item.price)}"
                rvProductList.layoutManager = LinearLayoutManager(
                    context, LinearLayoutManager.HORIZONTAL, false
                )
                val adapter = ImagesAdapter {}
                adapter.setData(item.image)
                rvProductList.adapter = adapter

                root.setOnClickListener { clickListener(item) }

                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup): ProductListViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemProductsListBinding.inflate(layoutInflater, parent, false)
                return ProductListViewHolder(binding)
            }
        }
    }

}