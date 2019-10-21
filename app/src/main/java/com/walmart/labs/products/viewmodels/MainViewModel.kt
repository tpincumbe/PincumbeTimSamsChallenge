package com.walmart.labs.products.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.walmart.labs.products.models.Product
import com.walmart.labs.products.views.ProductDetailPagerFragment
import com.walmart.labs.products.views.ProductListFragment

/**
 * TABLET VIEW ONLY
 * This is the model factory used to create the MainViewModel with a reference to if it's in tablet view or not
 */
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

/**
 * TABLET VIEW ONLY
 * This is the view model that will help keep the data when rotating the screen in tablet views
 */
class MainViewModel(private val isTwoPane: Boolean) : ViewModel() {
    val fragProductList: ProductListFragment = ProductListFragment.newInstance(isTwoPane)

    var fragProductDetail: ProductDetailPagerFragment?

    init {
        fragProductDetail = null
    }

    /**
     * THis is used when a product is selected in the product list. This will show the product in the detail fragment
     */
    fun updateProductDetailPage(position: Int, productList: MutableList<Product>) {
        fragProductDetail?.updateSelectedItem(position, productList) ?: run {
            fragProductDetail = ProductDetailPagerFragment.newInstance(position, productList, isTwoPane)
        }
    }
}