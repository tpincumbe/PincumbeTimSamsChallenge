package com.walmart.labs.products.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * The data model that represents a Walmart Product and it's properties
 */
@Parcelize
data class Product(
    val productId: String,
    val productName: String,
    val shortDescription: String?,
    val longDescription: String?,
    val price: String,
    val productImage: String,
    val reviewRating: Float,
    val reviewCount: Long,
    val inStock: Boolean
) : Parcelable