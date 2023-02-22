package com.example.products
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.products.activity.Add_Product_Activity
import com.example.products.baseActivity.BaseActivity
import com.example.products.baseActivity.ProductListActivity
import com.example.products.model.ProductsDataModel
import com.example.products.utils.Utils
import com.example.products.viewmodel.ProductViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import org.json.JSONObject

class MainActivity :  BaseActivity() {
    private lateinit var productListViewModel: ProductViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Utils.getInstance().isNetworkConnected(applicationContext)) {
            viewSetup()
        }else{
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("No internet Connection")
            builder.setMessage("Please turn on internet connection to continue")
            builder.setNegativeButton("close",
                DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
        }
    }

    private fun viewSetup() {
        findViewById<ImageView>(R.id.toolbar_menu_icon).setOnClickListener {
            startActivity(Intent(
                applicationContext,
                ProductListActivity::class.java
            ))
        }
        findViewById<FloatingActionButton>(R.id.add_product).setOnClickListener {
            startActivity(Intent(
                applicationContext,
                Add_Product_Activity::class.java
            ))
        }
        productListViewModel = ViewModelProvider.NewInstanceFactory().create(ProductViewModel()::class.java)
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
                        for (productData in response.products) {
                            findViewById<TextView>(R.id.txt_title).text = productData.title
                            findViewById<TextView>(R.id.txt_description).text =  productData.description
                            findViewById<TextView>(R.id.txt_price).text = "$ " +productData.price.toString()
                            findViewById<TextView>(R.id.txt_discount).text =  productData.discountPercentage.toString()+" % Discount"
                            findViewById<TextView>(R.id.txt_rating).text = productData.rating.toString()
                            findViewById<RatingBar>(R.id.rating_bar).rating=productData.rating.toFloat()
                            val url = productData.thumbnail.toString()
                            Glide.with(this)
                                .load(url)
                                .apply(RequestOptions().placeholder(R.drawable.no_image).error(R.drawable.no_image))
                                .into(findViewById(R.id.iv_image))
                        }
                    } else {
                        findViewById<TextView>(R.id.iv_image).text =""
                        findViewById<TextView>(R.id.txt_title).text = ""
                        findViewById<TextView>(R.id.txt_description).text = ""
                        findViewById<TextView>(R.id.txt_price).text = ""
                        findViewById<TextView>(R.id.txt_rating).text = ""

                        Glide.with(this)
                            .load(R.drawable.no_image)
                            .apply(RequestOptions().placeholder(R.drawable.no_image).error(R.drawable.no_image)
                            ).into(findViewById(R.id.iv_image))
                    }

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