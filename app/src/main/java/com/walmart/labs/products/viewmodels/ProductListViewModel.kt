package com.walmart.labs.products.viewmodels

import androidx.lifecycle.*
import com.walmart.labs.networking.ProductsApi
import com.walmart.labs.products.models.Product
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * This is the model factory used to create the ProductListViewModel with reference to the view type
 */
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

/**
 * This is the ViewModel used to represent the view data in the product list
 */
class ProductListViewModel(private val isTwoPane: Boolean) : ViewModel() {
    /**
     * This is used to interact with the fragment and show a snackbar error message
     */
    private val _errorMessage = MutableLiveData<Any?>()
    val errorMessage: LiveData<Any?> get() = _errorMessage

    /**
     * This is used as interaction with the layout / view to show the loading spinner when fetching additional data
     */
    private val _isLoadingAdditional = MutableLiveData<Boolean>().apply { value = false }
    val isLoadingAdditional: LiveData<Boolean> get() = _isLoadingAdditional

    /**
     * This is used as interaction with the layout / view to show the swipe refresh spinner
     */
    private val _isRefreshing = MutableLiveData<Boolean>().apply { value = true }
    val isRefreshing: LiveData<Boolean> get() = _isRefreshing

    /**
     * This is used for interaction with the fragment & layout for the product list
     */
    private val _productList = MutableLiveData<MutableList<Product>>()
    val productList: LiveData<MutableList<Product>> get() = _productList

    /*
     * Coroutine job & scope
     */
//    private var productJob = Job()
//    private var productScope = CoroutineScope(productJob + Dispatchers.IO)

    /**
     * The current page of data from the server
     */
    private var productPage = 1

    /**
     * If the server has additional data to fetch. This is set after loading data
     */
    var hasAdditionalProducts = false
        private set

    /**
     * The selected product position
     */
    var selectedProduct = 0

    init {
        fetchProducts(false)
    }

    /**
     * This uses a coroutine to call retrofit to get a page of data from the server
     * @param clearList - if we are refreshing the data this will clear the list of products
     */
    private fun fetchProducts(clearList: Boolean) {
//        if (productJob.isCancelled) {
//            productJob = Job()
//            productScope = CoroutineScope(productJob + Dispatchers.IO)
//        }

        viewModelScope.launch {
            try {
                val productsResponse = ProductsApi.fetchProducts(productPage)
                Timber.d("got products response $productsResponse")
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

    /**
     * This is used from the swipe layout to refresh the data and clear the list
     */
    fun refreshData() {
        _isRefreshing.value = true
        productPage = 1
        fetchProducts(true)
    }

    /**
     * This is used by the recycler view to fetch additional data when reaching the bottom of the list
     */
    fun fetchAddtionalProducts() {
        _isLoadingAdditional.value = true
        fetchProducts(false)
    }

    /**
     * This is used as interaction from the view / fragment to clear the error message
     */
    fun onErrorShown() {
        _errorMessage.value = null
    }

    /**
     * When the view model is "destroyed" we will cancel the coroutine job
     */
    override fun onCleared() {
        super.onCleared()
//        productJob.cancel()
    }
}