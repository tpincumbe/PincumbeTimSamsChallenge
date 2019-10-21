package com.walmart.labs.products.util

import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.walmart.labs.R
import com.walmart.labs.networking.BASE_URL
import com.walmart.labs.products.models.Product

/**
 * This file provides binding adapters for product layouts
 */

/**
 * This is used to set the background color of a selected product in the product list fragment
 */
@BindingAdapter("productListBackground")
fun bindProductListBackground(layout: ConstraintLayout, isSelected: Boolean) {
    layout.setBackgroundColor(
        ContextCompat.getColor(
            layout.context,
            if (isSelected) {
                R.color.highlight
            } else {
                android.R.color.white
            }
        )
    )
}

/**
 * This is used for the product descriptions since they come in as HTML strings
 */
@BindingAdapter("productHtml")
fun bindProductDesc(textView: TextView, description: String) {
    textView.text = HtmlCompat.fromHtml(
        description,
        HtmlCompat.FROM_HTML_MODE_COMPACT or HtmlCompat.FROM_HTML_OPTION_USE_CSS_COLORS
    )
}

/**
 * This is used to download the product image using glide
 */
@BindingAdapter("productImage")
fun bindProductImg(imageView: ImageView, imgUrl: String) {
    Glide
        .with(imageView.context)
        .load("$BASE_URL$imgUrl")
        .apply {
            placeholder(R.drawable.ic_dark_img_placeholder)
        }
        .into(imageView)
}

/**
 * This is used to bind the product list to the recycler view
 */
@BindingAdapter("productList")
fun bindProductList(recyclerView: RecyclerView, productList: MutableList<Product>?) {
    productList?.let {
        val adapter = recyclerView.adapter as ProductListAdapter
        adapter.submitList(it)
    }
}

/**
 * This is used to replace the unicode replace character found in some product names
 */
@BindingAdapter("productName")
fun bindProductName(textView: TextView, name: String) {
    textView.text = name.replace("\uFFFD", " ")
}