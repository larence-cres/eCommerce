package com.sample.ecommerce.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.sample.ecommerce.R
import com.sample.ecommerce.data.api.ApiUrls
import com.sample.ecommerce.databinding.ItemProductListImageBinding

class ImagesAdapter(
    private val longClickListener: (Int) -> Unit
) : RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder>() {

    private lateinit var images: List<String>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        return ImagesViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        holder.bind(images[position], longClickListener, position)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    fun setData(images: List<String>) {
        this.images = images
        notifyDataSetChanged()
    }

    class ImagesViewHolder(private val binding: ItemProductListImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String, longClickListener: (Int) -> Unit, position: Int) {

            var imageUrl = ""
            when {
                item.isNotEmpty() -> imageUrl = item.replace("localhost", ApiUrls.IP)
            }
            with(binding) {
                ivProduct.load(imageUrl) {
                    placeholder(R.drawable.ic_baseline_image_24)
                    error(R.drawable.ic_baseline_image_24)
                }

                root.setOnLongClickListener {
                    longClickListener(position)
                    true
                }

                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup): ImagesViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemProductListImageBinding.inflate(layoutInflater, parent, false)
                return ImagesViewHolder(binding)
            }
        }
    }

}