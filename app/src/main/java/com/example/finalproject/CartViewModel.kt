package com.example.finalproject

import android.net.Uri
import com.example.finalproject.ProductsViewModel

data class CartViewModel(
    val userId: String,
    val product: ProductsViewModel,
    var quantity: Int,
    val image: Uri
)
{

}
