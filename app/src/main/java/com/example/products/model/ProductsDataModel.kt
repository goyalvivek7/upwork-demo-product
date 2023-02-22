package com.example.products.model

data class ProductsDataModel(
    val total: Int?,
    val skip: Int?,
    val limit: Int?,
    val products: ArrayList<ProductsData>,
)

data class ProductsData(
    val id:Int,
    val title: String,
    val description: String,
    val price: Int,
    val discountPercentage: Float,
    val rating: Float,
    val stock: Int,
    val brand: String,
    val category: String,
    val thumbnail: String,
    val images: List<String>,
)