package com.walmart.labs.products.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.walmart.labs.databinding.ListItemProductBinding
import com.walmart.labs.products.models.Product
import com.walmart.labs.products.views.ProductViewHolder

class ProductListAdapter(private val clickListener: (Int) -> Unit) :
    ListAdapter<Product, ProductViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder =
        ProductViewHolder(
            ListItemProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) =
        holder.bindView(getItem(position), position, clickListener)

    companion object DiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean =
            oldItem === newItem

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean =
            oldItem.productId == newItem.productId

    }
}