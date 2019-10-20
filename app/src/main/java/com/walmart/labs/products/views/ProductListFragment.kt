package com.walmart.labs.products.views

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
import timber.log.Timber


class ProductListFragment : Fragment() {

    private lateinit var binding: FragmentProductListBinding

    private val viewModel: ProductListViewModel by lazy {
        ViewModelProviders.of(this).get(ProductListViewModel::class.java)
    }

    private val productListAdapter: ProductListAdapter by lazy {
        ProductListAdapter { product, position -> onProductTapped(product, position) }
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
    }

    private fun onProductTapped(product: Product, position: Int) {
        Timber.d("Tapped on ${product.productId}: ${product.productName}")
        findNavController().navigate(
            ProductListFragmentDirections
                .actionProductListFragmentToProductDetailPagerFragment(
                    viewModel.productList.value?.toTypedArray()!!,
                    position
                )
        )
    }

    private fun showSnackbarError(msg: String) {
        (activity as MainActivity).createSnackbar(msg)
    }
}
