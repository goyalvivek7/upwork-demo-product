package com.example.products.viewmodel
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.products.model.ProductsDataModel
import com.example.products.network.ApiResponse
import com.example.products.network.RetrofitClient
import com.example.products.network.RetrofitServices

class ProductViewModel() : ViewModel() {
    var getProductsForLiveData: LiveData<ApiResponse<ProductsDataModel>>? = null
    fun getProductsDataForUserData(context: Context?) {
        val service = RetrofitClient().getRetrofit(context).create(RetrofitServices::class.java)
        getProductsForLiveData = service.getProductsData()
    }
}