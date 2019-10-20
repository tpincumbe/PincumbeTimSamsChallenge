package com.walmart.labs.networking

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.walmart.labs.products.models.Product
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

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
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()

/**
 * A public interface that exposes functions to perform REST calls on [Products]
 */
interface ProductsApiService {
    @GET("/walmartproducts/{pageNumber}/{pageSize}")
    fun getProductsDeferred(@Path("pageNumber") pageNumber: Int, @Path("pageSize") pageSize: String = DEFAULT_PAGE_SIZE): Deferred<List<Product>>
}

/**
 * A public Api object that exposes the lazy-initialized Products Retrofit service
 */
object ProductsApi {
    val retrofitService: ProductsApiService by lazy { retrofit.create(ProductsApiService::class.java) }
}

class ProductsResponse(
    val products: List<Product>,
    val totalProducts: Long,
    val pageNumber: Int,
    val statusCode: Int
)