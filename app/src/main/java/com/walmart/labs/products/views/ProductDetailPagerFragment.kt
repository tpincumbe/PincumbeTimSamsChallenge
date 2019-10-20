package com.walmart.labs.products.views


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.walmart.labs.R
import com.walmart.labs.products.util.ProductDetailPagerAdapter
import kotlinx.android.synthetic.main.fragment_product_detail_pager.*

/**
 *
 */
class ProductDetailPagerFragment : Fragment() {

    companion object {
        fun newInstance() = ProductDetailFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_detail_pager, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        pagerProductDetail.adapter = ProductDetailPagerAdapter(fragmentManager!!)
    }
}