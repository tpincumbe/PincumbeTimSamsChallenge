package com.walmart.labs.products.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.walmart.labs.databinding.ListItemProductBinding
import com.walmart.labs.products.models.Product
import com.walmart.labs.products.views.ProductViewHolder

/**
 * This is the product list recycler view adapter. It uses [RecyclerView.ListAdapter] instead of [RecyclerView.Adapter]
 */
class ProductListAdapter(private val isTwoPane: Boolean, private val clickListener: (Int) -> Unit) :
    ListAdapter<Product, ProductViewHolder>(DiffCallback) {

    /**
     * TABLET VIEW ONLY
     * This is used to highlight the selected product
     */
    var selectedProduct = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder =
        ProductViewHolder(
            ListItemProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) =
        holder.bindView(
            getItem(position),
            position,
            (isTwoPane && selectedProduct == position),
            clickListener
        )

    companion object DiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean =
            oldItem === newItem

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean =
            oldItem.productId == newItem.productId

    }
}