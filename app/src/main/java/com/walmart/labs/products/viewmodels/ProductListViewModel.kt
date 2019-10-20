package com.walmart.labs.products.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.walmart.labs.networking.ProductsApi
import com.walmart.labs.products.models.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class ProductListViewModel : ViewModel() {
    private val _productList = MutableLiveData<MutableList<Product>>()
    val productList: LiveData<MutableList<Product>> get() = _productList

    private val _errorMessage = MutableLiveData<Any?>()
    val errorMessage: LiveData<Any?> get() = _errorMessage

    private val _isRefreshing = MutableLiveData<Boolean>().apply { value = true }
    val isRefreshing: LiveData<Boolean> get() = _isRefreshing

    private val productJob = Job()
    private val productScope = CoroutineScope(productJob + Dispatchers.IO)

    private var productPage = 1
    private var hasAdditonalProducts = false

    init {
        fetchProducts(false)
    }

    private fun fetchProducts(clearList: Boolean) {
        productScope.launch {
            try {
                val productsResponse = ProductsApi.fetchProducts(productPage)
                val fullList =
                    if (_productList.value == null) mutableListOf() else _productList.value!!
                if (clearList) {
                    fullList.clear()
                }
                fullList.addAll(productsResponse.products)
                hasAdditonalProducts = fullList.size < productsResponse.totalProducts
                Timber.d("full list: ${fullList.size}  total products: ${productsResponse.totalProducts}   has additional data: $hasAdditonalProducts")
                _productList.postValue(fullList)
            } catch (e: Exception) {
                Timber.e("Error getting products list: ${e.localizedMessage}")
                _errorMessage.postValue("Error getting product list. Please try again later.")
            }
            _isRefreshing.postValue(false)
        }
    }

    fun refreshData() {
        _isRefreshing.value = true
        productPage = 1
        fetchProducts(true)
    }

    fun onErrorShown() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        productJob.cancel()
    }
}