package com.example.products.adapter
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.products.R

class ProductViewPagerAdapter(
    private val arrayList: List<String>,
    private val context: Context
) : RecyclerView.Adapter<ProductViewPagerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem: View =
            layoutInflater.inflate(R.layout.item_slider_image, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val url = arrayList[position]
        Glide.with(context)
            .load(url)
            .apply(RequestOptions().placeholder(R.drawable.no_image).error(R.drawable.no_image))
            .into(holder.imageView)
    }
    override fun getItemCount(): Int {
        return arrayList.size
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView
        init {
            imageView = itemView.findViewById(R.id.image)
        }
    }
}