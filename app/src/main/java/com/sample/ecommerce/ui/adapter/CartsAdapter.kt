package com.sample.ecommerce.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.sample.ecommerce.R
import com.sample.ecommerce.data.api.ApiUrls
import com.sample.ecommerce.data.api.model.Cart
import com.sample.ecommerce.databinding.ItemCartsBinding
import com.sample.ecommerce.utils.currencyFormat
import timber.log.Timber

class CartsAdapter(
    private val clickListener: (Cart) -> Unit,
) : RecyclerView.Adapter<CartsAdapter.CartViewHolder>() {

    private lateinit var carts: ArrayList<Cart>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(carts[position], clickListener)
    }

    override fun getItemCount(): Int {
        return carts.size
    }

    fun setData(carts: ArrayList<Cart>) {
        this.carts = carts
        notifyDataSetChanged()
    }

    class CartViewHolder(private val binding: ItemCartsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var addedQuantity = 0

        fun bind(item: Cart, clickListener: (Cart) -> Unit) {

            var imageUrl = ""
            when {
                item.image.isNotEmpty() -> imageUrl = item.image.replace("localhost", ApiUrls.IP)
            }
            with(binding) {
                cart = item
                vh = this@CartViewHolder
                addedQuantity = item.quantity
                quantity = addedQuantity.toString()

                val price = item.price * item.quantity

                tvPrice.text = currencyFormat(price)

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

        fun increaseQuantity() {
            addedQuantity++
            Timber.e("Increase $addedQuantity")
        }

        fun decreaseQuantity() {
            addedQuantity--
            Timber.e("Decrease $addedQuantity")
        }

        companion object {
            fun from(parent: ViewGroup): CartViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemCartsBinding.inflate(layoutInflater, parent, false)
                return CartViewHolder(binding)
            }
        }
    }

}