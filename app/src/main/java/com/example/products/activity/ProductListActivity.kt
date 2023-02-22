package com.example.products.baseActivity
import ProductListAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.products.R
import com.example.products.activity.Add_Product_Activity
import com.example.products.model.ProductsDataModel
import com.example.products.utils.Utils
import com.example.products.viewmodel.ProductViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import org.json.JSONObject

open class ProductListActivity : BaseActivity() {
    private lateinit var productListViewModel: ProductViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productlist)
        viewSetup()
        findViewById<ImageView>(R.id.toolbar_menu_icon).setOnClickListener {
            onBackPressed()
        }

        findViewById<FloatingActionButton>(R.id.add_product).setOnClickListener {
            startActivity(
                Intent(
                applicationContext,
                Add_Product_Activity::class.java)
            )
        }

    }

    private fun viewSetup() {
        productListViewModel = ViewModelProvider.NewInstanceFactory().create(ProductViewModel()::class.java)
        findViewById<RecyclerView>(R.id.recycler_product_list).layoutManager = GridLayoutManager(this, 2)
        findViewById<RecyclerView>(R.id.recycler_product_list).isNestedScrollingEnabled = false
        findViewById<RecyclerView>(R.id.recycler_product_list).setHasFixedSize(true)
        if (Utils.getInstance().isNetworkConnected(applicationContext)) {
            progressBar.show(this)
            productListViewModel.getProductsDataForUserData(applicationContext)
            getProductsForUserResponse()
        } else {
            Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_LONG).show()
        }
    }
    private fun getProductsForUserResponse() {
        productListViewModel.getProductsForLiveData!!.observe(this, { tokenResponse ->
            try {
                val gson = Gson()
                val json = gson.toJson(tokenResponse)
                val jsonResponse = JSONObject(json)
                progressBar.dialog.dismiss()
                if (jsonResponse.has("body")) {
                    val body = jsonResponse.getJSONObject("body")
                    val response =
                        gson.fromJson(body.toString(), ProductsDataModel::class.java)
                    if (response.products.isNotEmpty()) {
                        findViewById<RecyclerView>(R.id.recycler_product_list).adapter =
                            ProductListAdapter(this, response.products)
                    } else { }
                } else {
                    if (jsonResponse.get("errorCode") == 500) {
                        showErrorDialog("Server is not responding")
                    } else {
                        showErrorDialog(jsonResponse.get("errorMessage").toString())
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }
    private fun showErrorDialog(message: String) {
        Toast.makeText(applicationContext, message.toString(), Toast.LENGTH_LONG).show()
    }
}