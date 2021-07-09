package com.sample.ecommerce.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sample.ecommerce.data.api.model.Product
import com.sample.ecommerce.databinding.ItemProductsHorizontalBinding
import java.util.*
import kotlin.collections.ArrayList

class MainAdapter(
    private val context: Context,
    private val clickListener: (Product) -> Unit,
    private val showMore: (String) -> Unit
) : RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    private lateinit var keys: ArrayList<String>
    private lateinit var products: ArrayList<List<Product>>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bind(products[position], keys[position], context, clickListener, showMore)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    fun setData(titles: ArrayList<String>, products: ArrayList<List<Product>>) {
        this.keys = titles
        this.products = products
    }

    class MainViewHolder(private val binding: ItemProductsHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: List<Product>?,
            key: String,
            context: Context,
            clickListener: (Product) -> Unit,
            showMore: (String) -> Unit
        ) {
            with(binding) {
                when {
                    key.equals("toppicks", true) -> {
                        tvTitle.text = "Top picks"
                        btnShowMore.visibility = View.GONE
                        val adapter = TopPicksAdapter { clickListener(it) }
                        adapter.setData(item!! as ArrayList<Product>)
                        rvProducts.adapter = adapter
                    }
                    else -> {
                        tvTitle.text = key.capitalize(Locale.ROOT)
                        val adapter = ProductsAdapter { clickListener(it) }
                        adapter.setData(item!! as ArrayList<Product>)
                        rvProducts.adapter = adapter
                    }
                }

                btnShowMore.setOnClickListener { showMore(key) }

                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup): MainViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemProductsHorizontalBinding.inflate(layoutInflater, parent, false)
                return MainViewHolder(binding)
            }
        }
    }

}