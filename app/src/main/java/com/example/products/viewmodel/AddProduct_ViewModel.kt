package com.example.products.viewmodel
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.products.model.ProductAdd_DataModel
import com.example.products.network.ApiResponse
import com.example.products.network.RetrofitClient
import com.example.products.network.RetrofitServices

class AddProduct_ViewModel() : ViewModel() {
    var addProductLiveData: LiveData<ApiResponse<ProductAdd_DataModel>>? = null
    fun getAddProductData(hashMap: HashMap<String, String>, context: Context?) {
        val service = RetrofitClient().getRetrofit(context).create(RetrofitServices::class.java)
        addProductLiveData = service.getsubmitSupportRequest(hashMap)
    }
}