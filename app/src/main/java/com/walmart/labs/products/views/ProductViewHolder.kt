package com.walmart.labs.products.views

import androidx.recyclerview.widget.RecyclerView
import com.walmart.labs.databinding.ListItemProductBinding
import com.walmart.labs.products.models.Product

class ProductViewHolder(private val binding: ListItemProductBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bindView(product: Product, position: Int, clickListener: (Product, Int) -> Unit) {
        binding.product = product
        binding.layoutProduct.setOnClickListener { clickListener.invoke(product, position) }
        binding.executePendingBindings()
    }
}