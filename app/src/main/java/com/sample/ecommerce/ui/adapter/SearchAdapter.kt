package com.sample.ecommerce.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.sample.ecommerce.R
import com.sample.ecommerce.data.api.ApiUrls
import com.sample.ecommerce.data.api.model.Product
import com.sample.ecommerce.databinding.ItemSearchBinding
import com.sample.ecommerce.utils.currencyFormat

class SearchAdapter(
    private val clickListener: (Product) -> Unit
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    private lateinit var products: ArrayList<Product>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(products[position], clickListener)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    fun setData(products: ArrayList<Product>) {
        this.products = products
        notifyDataSetChanged()
    }

    class SearchViewHolder(private val binding: ItemSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Product, clickListener: (Product) -> Unit) {

            binding.tvPrice.text = "Rs. ${currencyFormat(item.price)}"
            binding.product = item
            var imageUrl = ""
            when {
                item.image.isNotEmpty() -> imageUrl = item.image[0].replace("localhost", ApiUrls.IP)
            }
            with(binding) {
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
            fun from(parent: ViewGroup): SearchViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemSearchBinding.inflate(layoutInflater, parent, false)
                return SearchViewHolder(binding)
            }
        }
    }

}