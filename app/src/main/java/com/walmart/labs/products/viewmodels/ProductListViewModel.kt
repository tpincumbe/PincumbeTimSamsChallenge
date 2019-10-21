package com.walmart.labs.products.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.walmart.labs.networking.ProductsApi
import com.walmart.labs.products.models.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class ProductListViewModelFactory(private val isTwoPane: Boolean) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductListViewModel::class.java)) {
            return ProductListViewModel(isTwoPane) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

class ProductListViewModel(private val isTwoPane: Boolean) : ViewModel() {
    private val _errorMessage = MutableLiveData<Any?>()
    val errorMessage: LiveData<Any?> get() = _errorMessage

    private val _isLoadingAdditional = MutableLiveData<Boolean>().apply { value = false }
    val isLoadingAdditional: LiveData<Boolean> get() = _isLoadingAdditional

    private val _isRefreshing = MutableLiveData<Boolean>().apply { value = true }
    val isRefreshing: LiveData<Boolean> get() = _isRefreshing

    private val _productList = MutableLiveData<MutableList<Product>>()
    val productList: LiveData<MutableList<Product>> get() = _productList

    private val productJob = Job()
    private val productScope = CoroutineScope(productJob + Dispatchers.IO)

    private var productPage = 1
    var hasAdditionalProducts = false
        private set

    var selectedProduct = 0

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
                val newProducts = productsResponse.products
                fullList.addAll(newProducts)
                hasAdditionalProducts = fullList.size < productsResponse.totalProducts
                Timber.d("full list: ${fullList.size}  total products: ${productsResponse.totalProducts}   has additional data: $hasAdditionalProducts")
                _productList.postValue(fullList)
                productPage++
            } catch (e: Exception) {
                Timber.e("Error getting products list: ${e.localizedMessage}")
                _errorMessage.postValue("Error getting product list. Please try again later.")
            }
            _isRefreshing.postValue(false)
            _isLoadingAdditional.postValue(false)
        }
    }

    fun refreshData() {
        _isRefreshing.value = true
        productPage = 1
        fetchProducts(true)
    }

    fun fetchAddtionalProducts() {
        _isLoadingAdditional.value = true
        fetchProducts(false)
    }

    fun onErrorShown() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        productJob.cancel()
    }
}