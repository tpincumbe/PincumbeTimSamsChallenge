package com.walmart.labs.products.views


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.walmart.labs.MainActivity
import com.walmart.labs.databinding.FragmentProductDetailPagerBinding
import com.walmart.labs.products.models.Product
import com.walmart.labs.products.util.ProductDetailPagerAdapter
import java.util.*

const val SELECTED_PROD_TAG = "selectedProductTag"
const val PRODUCT_LIST_TAG = "productListTag"

/**
 * A fragment containing a view pager that displays product details
 * The view pager is used to swipe between products easily.
 */
class ProductDetailPagerFragment : Fragment() {

    private var binding: FragmentProductDetailPagerBinding? = null

    companion object {
        /**
         * Create a new ProductDetailPagerFragment with the supplied data
         * @param selectedProduct - the currently selected product to display first
         * @param productList - The product list to swipe through
         * @param isTwoPane - Used to update the UI depending on which view we're in
         */
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

    /**
     * The activity / fragment interaction listener object
     */
    private var mListener: OnFragmentInteractionListener? = null

    /**
     * Are we in a tablet view or phone view
     */
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
        binding = FragmentProductDetailPagerBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        /**
         * Get the data passed into the fragment
         */
        arguments?.let {
            isTwoPane = it.getBoolean(TWO_PANE_TAG, false)
            val position: Int
            val productsList: MutableList<Product>

            if (isTwoPane) { //Use the fragment extras to get the data
                position = it.getInt(SELECTED_PROD_TAG)
                productsList = it.getParcelableArrayList<Product>(PRODUCT_LIST_TAG)?.toMutableList()
                    ?: mutableListOf()
            } else { //Use the navigation controller to get the data
                val args = ProductDetailPagerFragmentArgs.fromBundle(it)
                position = args.selectedProductPos
                productsList = args.productList.toMutableList()
            }

            updateSelectedItem(position, productsList)
        }

        /**
         * Add the back button to the action bar
         */
        (activity as MainActivity?)?.apply {
            if (isTwoPane) {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            } else {
                title = "Product Details"
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                setHasOptionsMenu(true)
            }
        }
    }

    /**
     * This is used to set the 1st item to display product details for
     * @param position - the position of the selected product in the list
     * @param productsList - the product list to swipe through
     */
    fun updateSelectedItem(position: Int, productsList: MutableList<Product>) {
        //Update the fragment arguments. This is needed to save data on screen rotate
        arguments?.putInt(SELECTED_PROD_TAG, position)
        arguments?.putParcelableArrayList(PRODUCT_LIST_TAG, productsList as ArrayList)

        // Create and set the pager adapter properties
        binding?.pagerProductDetail?.apply {
            adapter = ProductDetailPagerAdapter(
                productsList,
                (activity as MainActivity).supportFragmentManager,
                lifecycle
            )
            currentItem = position
            if (isTwoPane) { // If in tablet view add a scroll listener to highlight the selected product in the list
                registerOnPageChangeCallback(onPageChangeCallback)
//                addOnPageChangeListener(object : OnPageChangeListener {
//                    override fun onPageScrollStateChanged(state: Int) {}
//
//                    override fun onPageScrolled(
//                        position: Int,
//                        positionOffset: Float,
//                        positionOffsetPixels: Int
//                    ) {
//                    }
//
//                    override fun onPageSelected(position: Int) {
//                        arguments?.putInt(SELECTED_PROD_TAG, position)
//                        mListener?.onPageSelected(position)
//                    }
//
//                })
            }
        }
    }

    private val onPageChangeCallback = object: OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            arguments?.putInt(SELECTED_PROD_TAG, position)
            mListener?.onPageSelected(position)
        }
    }

    /**
     * Overriding to provide action bar back button support
     */
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

    override fun onDestroy() {
        binding?.pagerProductDetail?.unregisterOnPageChangeCallback(onPageChangeCallback)
        binding = null
        super.onDestroy()
    }

    /**
     * Interface used to provide interaction between the activity and fragment
     */
    interface OnFragmentInteractionListener {
        fun goBack()

        fun onPageSelected(position: Int)
    }
}