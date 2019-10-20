package com.walmart.labs.products.views


import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.walmart.labs.MainActivity
import com.walmart.labs.R
import com.walmart.labs.products.models.Product
import com.walmart.labs.products.util.ProductDetailPagerAdapter
import kotlinx.android.synthetic.main.fragment_product_detail_pager.*
import timber.log.Timber

/**
 *
 */
class ProductDetailPagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_detail_pager, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.title = "Product Details"
        (activity as MainActivity).apply {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            setHasOptionsMenu(true)
        }

        arguments?.let {
            val args = ProductDetailPagerFragmentArgs.fromBundle(it)
            val position = args.selectedProductPos
            val productsList = args.productList.toMutableList()
            pagerProductDetail.apply {
                adapter = ProductDetailPagerAdapter(productsList, (activity as MainActivity).supportFragmentManager)
                currentItem = position
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            (activity as MainActivity).supportFragmentManager.popBackStack()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}