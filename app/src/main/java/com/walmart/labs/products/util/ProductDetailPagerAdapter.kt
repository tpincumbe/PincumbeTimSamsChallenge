package com.walmart.labs.products.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.walmart.labs.products.models.Product
import com.walmart.labs.products.views.ProductDetailFragment

/**
 * The adapter used to scroll product detail pages left and right
 */
class ProductDetailPagerAdapter(
    private val productsList: MutableList<Product>,
    manager: FragmentManager,
    lifecyle: Lifecycle
) : FragmentStateAdapter(manager, lifecyle) {
    override fun getItemCount(): Int =productsList.size

    override fun createFragment(position: Int): Fragment =
        ProductDetailFragment.newInstance(productsList[position])
}