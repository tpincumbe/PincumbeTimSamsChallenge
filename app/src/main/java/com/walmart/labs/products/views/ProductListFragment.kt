package com.walmart.labs.products.views

import android.content.Context
import android.nfc.tech.MifareUltralight.PAGE_SIZE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.walmart.labs.MainActivity
import com.walmart.labs.databinding.FragmentProductListBinding
import com.walmart.labs.products.models.Product
import com.walmart.labs.products.util.ProductListAdapter
import com.walmart.labs.products.viewmodels.ProductListViewModel
import com.walmart.labs.products.viewmodels.ProductListViewModelFactory
import timber.log.Timber


const val TWO_PANE_TAG = "twoPaneTag"

/**
 * A fragment containing the list of products
 */
class ProductListFragment : Fragment() {
    private var isTwoPane = false

    companion object {
        /**
         * Create a new ProductListFragment with the supplied data
         * @param isTwoPane - Used to update the UI depending on which view we're in
         */
        fun newInstance(isTwoPane: Boolean = false) =
            ProductListFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(TWO_PANE_TAG, isTwoPane)
                }
            }
    }

    /**
     * The data binding object for the product list fragment
     */
    private var binding: FragmentProductListBinding? = null

    /**
     * The factory to create the view model tha tis used for the product list
     */
    private val factory: ProductListViewModelFactory by lazy {
        ProductListViewModelFactory(isTwoPane)
    }

    /**
     * The viewmodel that represents the product list fragment
     */
    private val viewModel: ProductListViewModel by lazy {
        ViewModelProvider(this@ProductListFragment, factory).get(ProductListViewModel::class.java)
    }

    /**
     * The Reycler View List adapter for the product list
     */
    private val productListAdapter: ProductListAdapter by lazy {
        ProductListAdapter(isTwoPane) { position ->
            viewModel.selectedProduct = position
            if (isTwoPane) { // If tablet view then we will use fragment transactions
                updateListAdapterSelected(position)
                mListener?.onProductTapped(position, viewModel.productList.value ?: mutableListOf())
            } else { // If phone view then we will use the navigation controller
                viewModel.productList.value?.let {
                    findNavController().navigate(
                        ProductListFragmentDirections
                            .actionProductListFragmentToProductDetailPagerFragment(
                                it.toTypedArray(),
                                position
                            )
                    )
                }
//                findNavController().navigate(
//                    ProductListFragmentDirections
//                        .actionProductListFragmentToProductDetailPagerFragment(
//                            viewModel.productList.value?.toTypedArray()!!,
//                            position
//                        )
//                )
            }
        }
    }

    /**
     * The activity / fragment interaction listener object
     */
    private var mListener: OnFragmentInteractionListener? = null

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
        binding = FragmentProductListBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        isTwoPane = arguments?.getBoolean(TWO_PANE_TAG, false) ?: false
        activity?.title = "Walmart"
        binding?.lifecycleOwner = this
        binding?.viewModel = viewModel
        binding?.recyclerProductList?.apply {
            adapter = productListAdapter
            val manager = LinearLayoutManager(activity)
            layoutManager = manager
            context?.let { addItemDecoration(DividerItemDecoration(context, manager.orientation)) }
            // Add scroll listener to fetch additional data when we've reached the bottom of hte list
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val visibleItemCount = manager.childCount
                    val totalItemCount = manager.itemCount
                    val firstVisibleItemPosition = manager.findFirstVisibleItemPosition()

                    if (viewModel.hasAdditionalProducts && !viewModel.isLoadingAdditional.value!!) {
                        if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= PAGE_SIZE
                        ) {
                            Timber.d("Fetching Additional Products from scroll")
                            viewModel.fetchAddtionalProducts()
                        }
                    }
                }
            })
        }

        /**
         * When the error message object is update display a new snack bar message
         */
        viewModel.errorMessage.observe(viewLifecycleOwner, { error ->
            error?.let {
                when (it) {
                    is Int -> showSnackbarError(getString(it))
                    is String -> showSnackbarError(it)
                }
                viewModel.onErrorShown()
            }
        })

        /**
         * TABLET VIEW ONLY
         * When the product list is updated and is not empty then update the product detail fragment
         */
        viewModel.productList.observe(viewLifecycleOwner, { list ->
            if (list.isNotEmpty() && isTwoPane) {
                updateListAdapterSelected(viewModel.selectedProduct)
                mListener?.updateProductDetailPage(viewModel.selectedProduct, list)
            }
        })

        if (isTwoPane) {
            (activity as MainActivity?)?.apply {
                supportActionBar?.run {
                    setDisplayHomeAsUpEnabled(false)
                    setDisplayShowHomeEnabled(false)
                    setHomeButtonEnabled(false)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
//        activity?.actionBar?.setDisplayHomeAsUpEnabled(false)
//        setHasOptionsMenu(false)
    }

    /**
     * Helper function to display an error message as a snack bar
     */
    private fun showSnackbarError(msg: String) {
        (activity as MainActivity).createSnackbar(msg)
    }

    /***
     * TABLET VIEW ONLY
     * Helper function to update the selected product in the list
     */
    fun updateSelectedProduct(position: Int) {
        viewModel.selectedProduct = position
        updateListAdapterSelected(position)
    }

    /**
     * TABLET VIEW ONLY
     * This updated the Detail Page adapter to display the newly selected product
     */
    private fun updateListAdapterSelected(position: Int) {
        productListAdapter.apply {
            selectedProduct = position
            notifyDataSetChanged()
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    /**
     * Interface used to provide interaction between the activity and fragment
     */
    interface OnFragmentInteractionListener {
        fun onProductTapped(position: Int, productList: MutableList<Product>)

        fun updateProductDetailPage(position: Int, productList: MutableList<Product>)
    }
}
