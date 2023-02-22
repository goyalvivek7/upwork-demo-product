package com.example.products.network
import androidx.lifecycle.LiveData
import com.example.products.model.ProductsDataModel
import com.example.products.model.*
import retrofit2.http.*
interface RetrofitServices {
    @GET("products")
    fun getProductsData(
    ): LiveData<ApiResponse<ProductsDataModel>>

    @POST("products/add")
    fun getsubmitSupportRequest(
        @Body hashMap: HashMap<String, String>
    ): LiveData<ApiResponse<ProductAdd_DataModel>>
}