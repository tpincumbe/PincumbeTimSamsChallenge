package com.walmart.labs.networking

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.walmart.labs.products.models.Product
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import timber.log.Timber

const val BASE_URL = "https://mobile-tha-server.firebaseapp.com/"
const val DEFAULT_PAGE_SIZE = "30"

/**
 * The Moshi object that Retrofit will be using for JSON Conversion in Kotlin
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * The retrofit object to make network request using Moshi as a JSON converter.
 */
private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .addConverterFactory(GsonConverterFactory.create())
    .build()

/**
 * A public interface that exposes functions to perform REST calls on [ProductsResponse]
 */
interface ProductsApiService {
    @GET("/walmartproducts/{pageNumber}/{pageSize}")
    fun getProductsAsync(@Path("pageNumber") pageNumber: Int, @Path("pageSize") pageSize: String = DEFAULT_PAGE_SIZE): Deferred<ProductsResponse>
}

/**
 * A public Api object that exposes the lazy-initialized Products Retrofit service
 */
object ProductsApi {
    val retrofitService: ProductsApiService by lazy { retrofit.create(ProductsApiService::class.java) }

    suspend fun fetchProducts(productPage: Int): ProductsResponse {
        val deferredProducts = retrofitService.getProductsAsync(productPage)
        try {
            val productsResponse = deferredProducts.await()
            if (productsResponse.statusCode != 200) {
                Timber.e("Error fetching products list: ${productsResponse.statusCode}")
                throw (Exception("Error fetching products list"))
            } else {
                return productsResponse
            }
        } catch (e: Exception) {
            throw(e)
        }
    }
}

class ProductsResponse(
    val products: List<Product>,
    val totalProducts: Long,
    val pageNumber: Int,
    val statusCode: Int
) {
    override fun toString(): String =
        "Status: $statusCode pageNumber: $pageNumber totalProducts: $totalProducts productsList size: ${products.size}"
}