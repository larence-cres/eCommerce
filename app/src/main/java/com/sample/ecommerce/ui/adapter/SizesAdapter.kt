package com.sample.ecommerce.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sample.ecommerce.databinding.ItemSizesBinding
import kotlin.properties.Delegates

class SizesAdapter(
    private val clickListener: (String) -> Unit,
) : RecyclerView.Adapter<SizesAdapter.SizesViewHolder>() {

    private lateinit var sizes: List<String>

    var selectedPosition by Delegates.observable(-1) { property, oldPos, newPos ->
        if (newPos in sizes.indices) {
            notifyItemChanged(oldPos)
            notifyItemChanged(newPos)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizesViewHolder {
        return SizesViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SizesViewHolder, position: Int) {
        holder.bind(sizes[position], position == selectedPosition)
        holder.itemView.setOnClickListener {
            selectedPosition = position
            clickListener(sizes[position])
        }
    }

    override fun getItemCount(): Int {
        return sizes.size
    }

    fun setData(sizes: List<String>) {
        this.sizes = sizes
        notifyDataSetChanged()
    }

    class SizesViewHolder(private val binding: ItemSizesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String, selected: Boolean) {
            with(binding) {
                size = item
                ivSelect.visibility = when {
                    selected -> View.VISIBLE
                    else -> View.INVISIBLE
                }
                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup): SizesViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemSizesBinding.inflate(layoutInflater, parent, false)
                return SizesViewHolder(binding)
            }
        }
    }

}