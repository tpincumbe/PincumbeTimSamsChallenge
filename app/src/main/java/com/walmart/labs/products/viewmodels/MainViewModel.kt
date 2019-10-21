package com.walmart.labs.products.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.walmart.labs.products.models.Product
import com.walmart.labs.products.views.ProductDetailPagerFragment
import com.walmart.labs.products.views.ProductListFragment


class MainViewModelFactory(private val isTwoPane: Boolean) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(isTwoPane) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

class MainViewModel(private val isTwoPane: Boolean) : ViewModel() {
    val fragProductList: ProductListFragment = ProductListFragment.newInstance(isTwoPane)

    var fragProductDetail: ProductDetailPagerFragment?

    init {
        fragProductDetail = null
    }

    fun updateProductDetailPage(position: Int, productList: MutableList<Product>) {
        fragProductDetail?.updateSelectedItem(position, productList) ?: run {
            fragProductDetail = ProductDetailPagerFragment.newInstance(position, productList, isTwoPane)
        }
    }
}