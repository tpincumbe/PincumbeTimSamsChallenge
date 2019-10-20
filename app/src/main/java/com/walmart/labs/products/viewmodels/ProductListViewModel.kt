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

    private val _errorMessage = MutableLiveData<Any?>()
    val errorMessage: LiveData<Any?> get() = _errorMessage

    private val _isRefreshing = MutableLiveData<Boolean>().apply { value = false }
    val isRefreshing: LiveData<Boolean> get() = _isRefreshing

    private val productJob = Job()
    private val productScope = CoroutineScope(productJob + Dispatchers.IO)

    private var productPage = 1

    init {
        fetchProducts(false)
    }

    private fun fetchProducts(clearList: Boolean) {
        productScope.launch {
            try {
                val productList = ProductsApi.fetchProducts(productPage)
                val fullList = _productList.value
                if (clearList) {
                    fullList?.clear()
                }
                fullList?.addAll(productList)
                _productList.postValue(fullList)
                _isRefreshing.postValue(false)
            } catch (e: Exception) {
                Timber.e("Error getting products list: ${e.localizedMessage}")
                _errorMessage.postValue("Error getting product list. Please try again later.")
            }
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