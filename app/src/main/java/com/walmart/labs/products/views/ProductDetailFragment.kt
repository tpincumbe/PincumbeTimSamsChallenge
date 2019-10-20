package com.walmart.labs.products.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.walmart.labs.MainActivity
import com.walmart.labs.R
import com.walmart.labs.databinding.FragmentProductDetailBinding
import com.walmart.labs.products.models.Product
import com.walmart.labs.products.viewmodels.ProductDetailViewModel
import timber.log.Timber

private const val PRODUCT_TAG = "productTag"

class ProductDetailFragment : Fragment() {

    companion object {
        fun newInstance(product: Product): ProductDetailFragment {
            val frag = ProductDetailFragment()
            val bundle = Bundle()
            bundle.putParcelable(PRODUCT_TAG, product)
            frag.arguments = bundle
            return frag
        }
    }

    private lateinit var binding: FragmentProductDetailBinding

    private val viewModel: ProductDetailViewModel by lazy {
        ViewModelProviders.of(this).get(ProductDetailViewModel::class.java)
    }

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
        binding.lifecycleOwner = this
        val product = arguments?.getParcelable<Product>(PRODUCT_TAG)
        if (product == null) {
            Timber.e("Product parcelable is null when creating detail fragment")
            fragmentManager?.popBackStack()
            showSnackbarError("Error loading product details. Please try again later.")
        }
        binding.product = product
    }

    private fun showSnackbarError(msg: String) {
        (activity as MainActivity).createSnackbar(msg)
    }
}