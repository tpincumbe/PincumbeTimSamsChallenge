package com.walmart.labs.products.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.walmart.labs.MainActivity
import com.walmart.labs.R
import com.walmart.labs.databinding.FragmentProductDetailBinding
import com.walmart.labs.products.models.Product
import timber.log.Timber

private const val PRODUCT_TAG = "productTag"

/**
 * This fragment represents a single product detail view
 * This is used in the view pager to swipe between products
 */
class ProductDetailFragment : Fragment() {

    companion object {
        /**
         * Create a new fragment to pass in a product
         * @param product - the product to display
         */
        fun newInstance(product: Product): ProductDetailFragment {
            val frag = ProductDetailFragment()
            val bundle = Bundle()
            bundle.putParcelable(PRODUCT_TAG, product)
            frag.arguments = bundle
            return frag
        }
    }

    /**
     * Binding variable for the detail page
     */
    private lateinit var binding: FragmentProductDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_product_detail, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.let {
            it.lifecycleOwner = this
            val product = arguments?.getParcelable<Product>(PRODUCT_TAG)
            if (product == null) {
                Timber.e("Product parcelable is null when creating detail fragment")
                fragmentManager?.popBackStack()
                showSnackbarError("Error loading product details. Please try again later.")
            }
            it.product = product
        }
    }

    /**
     * A helper function used to display a snackbar error message
     */
    private fun showSnackbarError(msg: String) {
        (activity as MainActivity).createSnackbar(msg)
    }
}