package com.walmart.labs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.walmart.labs.products.models.Product
import com.walmart.labs.products.views.ProductDetailPagerFragment
import com.walmart.labs.products.views.ProductListFragment
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

const val PRODUCT_DETAIL_TAG = "productDetail"

class MainActivity : AppCompatActivity(), ProductListFragment.OnFragmentInteractionListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private val isTwoPane: Boolean by lazy {
        frag_product_details != null
    }

    private val fragProductList: ProductListFragment by lazy {
        ProductListFragment.newInstance(isTwoPane)
    }

    private var fragProductDetail: ProductDetailPagerFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.frag_product_list, fragProductList)
            .commit()
    }

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

    override fun onProductTapped(position: Int, productList: MutableList<Product>) {
        Timber.d("Tapped on ${productList[position].productId}: ${productList[position].productName}")
        if (isTwoPane) {
            fragProductDetail?.updateSelectedItem(position, productList)
        } else {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.frag_product_list,
                    ProductDetailPagerFragment.newInstance(position, productList)
                )
                .addToBackStack(PRODUCT_DETAIL_TAG)
                .commit()
        }
    }

    override fun updateProductDetailPage(position: Int, productList: MutableList<Product>) {
        if (fragProductDetail == null) {
            fragProductDetail = ProductDetailPagerFragment.newInstance(position, productList).apply {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frag_product_details, this)
                    .commit()
            }
        }
    }
}