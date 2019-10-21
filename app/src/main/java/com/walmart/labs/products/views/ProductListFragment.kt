package com.walmart.labs.products.views

import android.content.Context
import android.nfc.tech.MifareUltralight.PAGE_SIZE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.walmart.labs.MainActivity
import com.walmart.labs.R
import com.walmart.labs.databinding.FragmentProductListBinding
import com.walmart.labs.products.models.Product
import com.walmart.labs.products.util.ProductListAdapter
import com.walmart.labs.products.viewmodels.ProductListViewModel
import com.walmart.labs.products.viewmodels.ProductListViewModelFactory
import timber.log.Timber


const val TWO_PANE_TAG = "twoPaneTag"

class ProductListFragment : Fragment() {
    private var isTwoPane = false

    companion object {
        fun newInstance(isTwoPane: Boolean = false) =
            ProductListFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(TWO_PANE_TAG, isTwoPane)
                }
            }
    }

    private lateinit var binding: FragmentProductListBinding

    private val factory: ProductListViewModelFactory by lazy {
        ProductListViewModelFactory(isTwoPane)
    }
    private val viewModel: ProductListViewModel by lazy {
        ViewModelProviders.of(this, factory).get(ProductListViewModel::class.java)
    }

    private val productListAdapter: ProductListAdapter by lazy {
        ProductListAdapter(isTwoPane) { position ->
            viewModel.selectedProduct = position
            if (isTwoPane) {
                updateListAdapterSelected(position)
                mListener?.onProductTapped(position, viewModel.productList.value ?: mutableListOf())
            } else {
                findNavController().navigate(
                    ProductListFragmentDirections
                        .actionProductListFragmentToProductDetailPagerFragment(
                            viewModel.productList.value?.toTypedArray()!!,
                            position
                        )
                )
            }
        }
    }

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
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_product_list, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        isTwoPane = arguments?.getBoolean(TWO_PANE_TAG, false) ?: false
        activity?.title = "Walmart"
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.recyclerProductList.apply {
            adapter = productListAdapter
            val manager = LinearLayoutManager(activity)
            layoutManager = manager
            context?.let { addItemDecoration(DividerItemDecoration(context, manager.orientation)) }
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

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                when (it) {
                    is Int -> showSnackbarError(getString(it))
                    is String -> showSnackbarError(it)
                }
                viewModel.onErrorShown()
            }
        })

        viewModel.productList.observe(viewLifecycleOwner, Observer {list ->
            if (list.isNotEmpty() && isTwoPane) {
                updateListAdapterSelected(viewModel.selectedProduct)
                mListener?.updateProductDetailPage(viewModel.selectedProduct, list)
            }
        })
    }

    private fun showSnackbarError(msg: String) {
        (activity as MainActivity).createSnackbar(msg)
    }

    fun updateSelectedProduct(position: Int) {
        viewModel.selectedProduct = position
        updateListAdapterSelected(position)
    }

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

    interface OnFragmentInteractionListener {
        fun onProductTapped(position: Int, productList: MutableList<Product>)

        fun updateProductDetailPage(position: Int, productList: MutableList<Product>)
    }
}
