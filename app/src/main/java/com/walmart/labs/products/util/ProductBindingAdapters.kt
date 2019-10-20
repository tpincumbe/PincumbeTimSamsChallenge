package com.walmart.labs.products.util

import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.walmart.labs.R
import com.walmart.labs.networking.BASE_URL
import com.walmart.labs.products.models.Product
import timber.log.Timber

@BindingAdapter("productHtml")
fun bindProductDesc(textView: TextView, description: String) {
    textView.text = HtmlCompat.fromHtml(
        description,
        HtmlCompat.FROM_HTML_MODE_COMPACT or HtmlCompat.FROM_HTML_OPTION_USE_CSS_COLORS
    )
}

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

@BindingAdapter("productList")
fun bindProductList(recyclerView: RecyclerView, productList: MutableList<Product>?) {
    productList?.let {
        val adapter = recyclerView.adapter as ProductListAdapter
        adapter.submitList(it)
    }
}