package com.walmart.labs.products.views


import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.walmart.labs.MainActivity
import com.walmart.labs.R
import com.walmart.labs.products.models.Product
import com.walmart.labs.products.util.ProductDetailPagerAdapter
import kotlinx.android.synthetic.main.fragment_product_detail_pager.*
import java.util.*

const val SELECTED_PROD_TAG = "selectedProductTag"
const val PRODUCT_LIST_TAG = "productListTag"

/**
 *
 */
class ProductDetailPagerFragment : Fragment() {

    companion object {
        fun newInstance(
            selectedProduct: Int,
            productList: MutableList<Product>
        ): ProductDetailPagerFragment =
            ProductDetailPagerFragment().apply {
                arguments = Bundle().apply {
                    putInt(SELECTED_PROD_TAG, selectedProduct)
                    putParcelableArrayList(PRODUCT_LIST_TAG, productList as ArrayList)
                }
            }
    }

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
            val position = it.getInt(SELECTED_PROD_TAG)
            val productsList = it.getParcelableArrayList<Product>(PRODUCT_LIST_TAG)?.toMutableList()
                ?: mutableListOf()
            updateSelectedItem(position, productsList)
        }
    }

    fun updateSelectedItem(position: Int, productsList: MutableList<Product>) {
        arguments?.putInt(SELECTED_PROD_TAG, position)
        arguments?.putParcelableArrayList(PRODUCT_LIST_TAG, productsList as ArrayList)

        pagerProductDetail.apply {
            adapter = ProductDetailPagerAdapter(
                productsList,
                (activity as MainActivity).supportFragmentManager
            )
            currentItem = position
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