package com.walmart.labs

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.google.android.material.snackbar.Snackbar
import com.walmart.labs.databinding.ActivityMainBinding
import com.walmart.labs.products.models.Product
import com.walmart.labs.products.viewmodels.MainViewModel
import com.walmart.labs.products.viewmodels.MainViewModelFactory
import com.walmart.labs.products.views.ProductDetailPagerFragment
import com.walmart.labs.products.views.ProductListFragment
import timber.log.Timber

/**
 * The entry point to the app. This will handle both tablet views and phone views.
 * For the phone view this will use the Android Navigation Controller
 */
class MainActivity : AppCompatActivity(), ProductListFragment.OnFragmentInteractionListener,
    ProductDetailPagerFragment.OnFragmentInteractionListener {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device. Checks if the layout file contains the frag product details frame layout.
     */
    private val isTwoPane: Boolean by lazy {
        binding.fragProductDetails != null
    }

    /**
     * TABLET VIEW ONLY
     * The factory to create the view model that is used for the Tablet view
     */
    private val factory: MainViewModelFactory by lazy {
        MainViewModelFactory(isTwoPane)
    }

    /**
     * TABLET VIEW ONLY
     * The viewmodel that represents the main activity.
     */
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this@MainActivity, factory).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        /*
         * If we are in a tablet view then load both fragments. Otherwise the navigation controller takes control
         */
        if (isTwoPane) {
            loadProductListPage()
            loadProductDetailPage()
        }
    }

    /**
     * A helper function that will create a red snackbar to show an error message with a close button
     * @param msg - the message string to put into the snacbar
     */
    fun createSnackbar(msg: String) {
        if (msg.isNotEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_INDEFINITE)
                .apply {
                    setAction("DISMISS") {
                        dismiss()
                    }
                    setActionTextColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            android.R.color.white
                        )
                    )
                    view.setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.red))
                    show()
                }
        }
    }

    /**
     * * TABLET VIEW ONLY
     * A function to provide interaction with this Activity from the fragment.
     * This is only used for tablet views. When in tablet view this will update the right view to show
     * the newly selected product
     */
    override fun onProductTapped(position: Int, productList: MutableList<Product>) {
        Timber.d("Tapped on ${productList[position].productId}: ${productList[position].productName}")
        if (isTwoPane) {
            viewModel.fragProductDetail?.updateSelectedItem(position, productList)
        }
    }

    /**
     * This is a helper function to update the product detail page in Tablet views only
     */
    override fun updateProductDetailPage(position: Int, productList: MutableList<Product>) {
        viewModel.updateProductDetailPage(position, productList)
        loadProductDetailPage()
    }

    /**
     * TABLET VIEW ONLY
     * This will use the fragment manager to load the product list fragment on the left
     */
    private fun loadProductListPage() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frag_product_list, viewModel.fragProductList)
            .commit()
    }

    /**
     * TABLET VIEW ONLY
     * This will use the fragment manager to load the product detail fragment on the right
     */
    private fun loadProductDetailPage() {
        viewModel.fragProductDetail?.let {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frag_product_details, it)
                .commit()
        }
    }

    /**
     * TABLET VIEW ONLY
     * This provide fragment interaction with the detail page. It will update the selected product in
     * the product list fragment
     */
    override fun onPageSelected(position: Int) {
        viewModel.fragProductList.updateSelectedProduct(position)
    }

    inner class PaneListener : SlidingPaneLayout.PanelSlideListener {
        override fun onPanelSlide(panel: View, slideOffset: Float) {
            TODO("Not yet implemented")
        }

        override fun onPanelOpened(panel: View) {
            TODO("Not yet implemented")
        }

        override fun onPanelClosed(panel: View) {
            TODO("Not yet implemented")
        }

    }
}