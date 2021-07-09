package com.sample.ecommerce.ui.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sample.ecommerce.databinding.ItemColorsBinding
import kotlin.properties.Delegates

class ColorsAdapter(
    private val context: Context,
    private val selectable: Boolean,
    private val clickListener: (String) -> Unit,
    private val longClickListener: (Int) -> Unit,
) : RecyclerView.Adapter<ColorsAdapter.ColorsViewHolder>() {

    private lateinit var colors: List<String>

    var selectedPosition by Delegates.observable(-1) { property, oldPos, newPos ->
        if (newPos in colors.indices) {
            notifyItemChanged(oldPos)
            notifyItemChanged(newPos)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorsViewHolder {
        return ColorsViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ColorsViewHolder, position: Int) {
        holder.bind(context,
            colors[position],
            longClickListener,
            position == selectedPosition,
            selectable)
        holder.itemView.setOnClickListener {
            selectedPosition = position
            clickListener(colors[position])
        }
    }

    override fun getItemCount(): Int {
        return colors.size
    }

    fun setData(colors: List<String>) {
        this.colors = colors
        notifyDataSetChanged()
    }

    class ColorsViewHolder(private val binding: ItemColorsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            context: Context,
            item: String,
            longClickListener: (Int) -> Unit,
            selected: Boolean,
            selectable: Boolean,
        ) {
            with(binding) {
                val intColor = Color.parseColor(item)
                color = intColor
                when {
                    selectable -> ivSelect.visibility = when {
                        selected -> View.VISIBLE
                        else -> View.INVISIBLE
                    }
                }
                root.setOnLongClickListener {
                    longClickListener(bindingAdapterPosition)
                    true
                }

                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup): ColorsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemColorsBinding.inflate(layoutInflater, parent, false)
                return ColorsViewHolder(binding)
            }
        }
    }

}