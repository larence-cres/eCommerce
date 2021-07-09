package com.sample.ecommerce.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.sample.ecommerce.R
import com.sample.ecommerce.data.api.ApiUrls
import com.sample.ecommerce.data.api.model.Product
import com.sample.ecommerce.databinding.ItemProductImageBinding
import timber.log.Timber

class ImageAdapter(
    private val images: ArrayList<String>,
    private val productId: String
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    private lateinit var products: ArrayList<Product>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(images[position], productId)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    fun setData(products: ArrayList<Product>) {
        this.products = products
        notifyDataSetChanged()
    }

    class ImageViewHolder(private val binding: ItemProductImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String, productId: String) {

            ViewCompat.setTransitionName(binding.ivProduct, "image_$productId")
            var imageUrl = ""
            when {
                item.isNotEmpty() -> imageUrl = item.replace("localhost", ApiUrls.IP)
            }
            Timber.e("url $imageUrl")
            with(binding) {
                ivProduct.load(imageUrl) {
                    placeholder(R.drawable.ic_baseline_image_24)
                    error(R.drawable.ic_baseline_image_24)
                }
                executePendingBindings()
            }

        }

        companion object {
            fun from(parent: ViewGroup): ImageViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemProductImageBinding.inflate(layoutInflater, parent, false)
                return ImageViewHolder(binding)
            }
        }
    }

}