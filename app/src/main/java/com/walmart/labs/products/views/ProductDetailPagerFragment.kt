package com.walmart.labs.products.views


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
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
            productList: MutableList<Product>,
            isTwoPane: Boolean
        ): ProductDetailPagerFragment =
            ProductDetailPagerFragment().apply {
                arguments = Bundle().apply {
                    putInt(SELECTED_PROD_TAG, selectedProduct)
                    putParcelableArrayList(PRODUCT_LIST_TAG, productList as ArrayList)
                    putBoolean(TWO_PANE_TAG, isTwoPane)
                }
            }
    }

    private var mListener: OnFragmentInteractionListener? = null

    private var isTwoPane = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(" $context must implement OnFragmentInteractionListener")
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
            isTwoPane = it.getBoolean(TWO_PANE_TAG, false)
            val position: Int
            val productsList: MutableList<Product>

            if (isTwoPane) {
                position = it.getInt(SELECTED_PROD_TAG)
                productsList = it.getParcelableArrayList<Product>(PRODUCT_LIST_TAG)?.toMutableList()
                    ?: mutableListOf()
            } else {
                val args = ProductDetailPagerFragmentArgs.fromBundle(it)
                position = args.selectedProductPos
                productsList = args.productList.toMutableList()
            }

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
            if (isTwoPane) {
                addOnPageChangeListener(object : OnPageChangeListener {
                    override fun onPageScrollStateChanged(state: Int) {}

                    override fun onPageScrolled(
                        position: Int,
                        positionOffset: Float,
                        positionOffsetPixels: Int
                    ) {
                    }

                    override fun onPageSelected(position: Int) {
                        arguments?.putInt(SELECTED_PROD_TAG, position)
                        mListener?.onPageSelected(position)
                    }

                })
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            mListener?.goBack()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnFragmentInteractionListener {
        fun goBack()

        fun onPageSelected(position: Int)
    }
}