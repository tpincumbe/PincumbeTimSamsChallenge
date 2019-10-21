package com.walmart.labs.products.views

import androidx.recyclerview.widget.RecyclerView
import com.walmart.labs.databinding.ListItemProductBinding
import com.walmart.labs.products.models.Product

/**
 * Class the represents a single list item view in a recycler of products
 */
class ProductViewHolder(private val binding: ListItemProductBinding) :
    RecyclerView.ViewHolder(binding.root) {

    /**
     * Bind the supply data to the layout data binding
     */
    fun bindView(product: Product, position: Int, isSelected: Boolean, clickListener: (Int) -> Unit) {
        binding.product = product
        binding.isSelected = isSelected
        binding.layoutProduct.setOnClickListener { clickListener.invoke(position) }
        binding.executePendingBindings()
    }
}