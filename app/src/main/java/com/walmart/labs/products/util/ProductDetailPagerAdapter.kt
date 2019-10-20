package com.walmart.labs.products.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.walmart.labs.products.views.ProductDetailFragment

class ProductDetailPagerAdapter(manager: FragmentManager) :
    FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragmentList = mutableListOf<ProductDetailFragment>()

    override fun getItem(position: Int): Fragment = fragmentList[position]

    override fun getCount(): Int = fragmentList.size

//    override fun getPageTitle(position: Int): CharSequence? = fragmentList[position].getTitle()

    fun addFragment(frag: ProductDetailFragment) = fragmentList.add(frag)
}