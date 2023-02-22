package com.example.products.network
import android.content.Context
import com.example.products.utils.Constant
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitClient {
    private val interceptor = run {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.apply {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        }
    }
    var serverUrl = Constant().baseUrl
    val okHttpClient = OkHttpClient.Builder()
        .connectionPool(ConnectionPool(0, 1, TimeUnit.NANOSECONDS))
        .protocols(listOf(Protocol.HTTP_1_1))
        .addNetworkInterceptor(interceptor) // same for .addInterceptor(...)
        .readTimeout(300, TimeUnit.SECONDS)
        .connectTimeout(300, TimeUnit.SECONDS)
        .writeTimeout(300,TimeUnit.SECONDS)
        .retryOnConnectionFailure(false)
        .build()
    var token = ""
    fun getRetrofit(context: Context?): Retrofit {
        return Retrofit.Builder()
            .baseUrl(serverUrl)
            .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient)
            .client(OkHttpClient.Builder().addInterceptor { chain ->
                val request =
                    chain.request().newBuilder().addHeader("Authorization", token)
                        .build()
                chain.proceed(request)
            }.build())
            .addCallAdapterFactory(LiveDataCallAdapterFactory()).build()
    }
}