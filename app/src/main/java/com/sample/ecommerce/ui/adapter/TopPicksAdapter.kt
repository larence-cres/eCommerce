package com.sample.ecommerce.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.sample.ecommerce.R
import com.sample.ecommerce.data.api.ApiUrls
import com.sample.ecommerce.data.api.model.Product
import com.sample.ecommerce.databinding.ItemTopPicksBinding

class TopPicksAdapter(
    private val clickListener: (Product) -> Unit
) : RecyclerView.Adapter<TopPicksAdapter.TopPicksViewHolder>() {

    private lateinit var topPicks: ArrayList<Product>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopPicksViewHolder {
        return TopPicksViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TopPicksViewHolder, position: Int) {
        holder.bind(topPicks[position], clickListener)
    }

    override fun getItemCount(): Int {
        return topPicks.size
    }

    fun setData(topPicks: ArrayList<Product>) {
        this.topPicks = topPicks
        notifyDataSetChanged()
    }

    class TopPicksViewHolder(private val binding: ItemTopPicksBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Product, clickListener: (Product) -> Unit) {

            binding.topPicks = item
            var imageUrl = ""
            when {
                item.image.isNotEmpty() -> imageUrl = item.image[0].replace("localhost", ApiUrls.IP)
            }
            with(binding) {
                ivTopPick.load(imageUrl) {
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
            fun from(parent: ViewGroup): TopPicksViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemTopPicksBinding.inflate(layoutInflater, parent, false)
                return TopPicksViewHolder(binding)
            }
        }
    }

}