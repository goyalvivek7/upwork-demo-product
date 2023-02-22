package com.example.products.adapter
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.products.R
import com.example.products.databinding.ItemAddProductBinding
import java.util.*
class ImageProductAdapter(
    private val context: Context,
    private val arrayList: ArrayList<Uri>,
    private val imageMain: ImageView,
    private val isUri: Boolean,
    private val listener: EventListener
) :
    RecyclerView.Adapter<ImageProductAdapter.ViewHolder>() {
    interface EventListener {
        fun onEvent(data: String,position: kotlin.Int)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemAddProductBinding =
            ItemAddProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(isUri){
            Glide.with(context)
                .load(Uri.parse(arrayList[0].toString()))
                .apply(
                    RequestOptions().placeholder(R.drawable.no_image)
                        .error(R.drawable.no_image)
                )
                .into(imageMain)
        }
        if(isUri){
            Glide.with(context)
                .load(Uri.parse(arrayList[position].toString()).toString())
                .apply(
                    RequestOptions().placeholder(R.drawable.no_image)
                        .error(R.drawable.no_image)
                )
                .into(holder.image)
        }else{
            Glide.with(context)
                .load(arrayList[position])
                .apply(
                    RequestOptions().placeholder(R.drawable.no_image)
                        .error(R.drawable.no_image)
                )
                .into(holder.image)
        }
        holder.image.setOnClickListener {
            if(isUri){
                Glide.with(context)
                    .load(Uri.parse(arrayList[position].toString()).toString())
                    .apply(
                        RequestOptions().placeholder(R.drawable.no_image)
                            .error(R.drawable.no_image)
                    )
                    .into(imageMain)
            }else{
                Glide.with(context)
                    .load(arrayList[position])
                    .apply(
                        RequestOptions().placeholder(R.drawable.no_image)
                            .error(R.drawable.no_image)
                    )
                    .into(imageMain)
            }
        }
        if(isUri){
            holder.remove.visibility = View.VISIBLE
        }else{
            holder.remove.visibility = View.VISIBLE
        }
        holder.remove.setOnClickListener {
            listener.onEvent(arrayList[position].toString(),position)
        }
    }
    override fun getItemCount(): Int {
        return arrayList.size
    }
    inner class ViewHolder(binding: ItemAddProductBinding) : RecyclerView.ViewHolder(binding.root) {
        var image: ImageView = binding.image
        var remove: ImageView = binding.remove
    }
}