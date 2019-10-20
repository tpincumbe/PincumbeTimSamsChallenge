package com.walmart.labs.products.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.walmart.labs.products.models.Product
import com.walmart.labs.products.views.ProductDetailFragment

class ProductDetailPagerAdapter(
    private val productsList: MutableList<Product>,
    manager: FragmentManager
) : FragmentStatePagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment =
        ProductDetailFragment.newInstance(productsList[position])

    override fun getCount(): Int = productsList.size
}