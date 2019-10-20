package com.walmart.labs.products.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.walmart.labs.networking.ProductsApi
import com.walmart.labs.products.models.Product
import kotlinx.coroutines.*
import timber.log.Timber

class ProductListViewModel : ViewModel() {
    private val _productList =
        MutableLiveData<MutableList<Product>>().apply { value = mutableListOf() }
    val productList: LiveData<MutableList<Product>> get() = _productList

    private val productJob = Job()
    private val productScope = CoroutineScope(productJob + Dispatchers.IO)

    private var productPage = 1

    init {
        fetchProducts(false)
    }

    private fun fetchProducts(clearList: Boolean) {
        productScope.launch {
            val deferredProducts = ProductsApi.retrofitService.getProductsDeferred(productPage)
            try {
                val productList = deferredProducts.await()
                val fullList = _productList.value
                if (clearList) {
                    fullList?.clear()
                }
                fullList?.addAll(productList)
                _productList.postValue(fullList)
            } catch (e: Exception) {
                Timber.e("Error getting products list: ${e.localizedMessage}")
                TODO("Show snackbar")
            }
        }
    }

    fun refreshData() {
        productPage = 1
        fetchProducts(true)
    }

    override fun onCleared() {
        super.onCleared()
        productJob.cancel()
    }
}