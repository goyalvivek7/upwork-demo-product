import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.products.R
import com.example.products.adapter.ProductViewPagerAdapter
import com.example.products.model.ProductsData
import java.util.*

class ProductListAdapter(
    private val context: Context,
    private val arrayList: ArrayList<ProductsData>
) :
    RecyclerView.Adapter<ProductListAdapter.ViewHolder>() {
    private var runnableBanner: Runnable? = null
    private val handlerBanner: Handler = Handler(Looper.getMainLooper())

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem: View =
            layoutInflater.inflate(R.layout.item_product, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item_product_text_title.text = arrayList[position].title
        holder.item_product_text_price.text = "$ "+arrayList[position].price.toString()
        holder.item_product_text_rating.text = arrayList[position].rating.toString()
        holder.item_product_text_ratingBar.rating=arrayList[position].rating.toFloat()

        val adapterBanner = ProductViewPagerAdapter(arrayList[position].images, context)
        holder.item_product_view_pager.adapter = adapterBanner
        holder.item_product_view_pager.setPageTransformer(MarginPageTransformer(15))
        holder.item_product_view_pager.registerOnPageChangeCallback(bannerPageChangeCallback)
        bottomProgressDotsBanner(0,holder.item_product_view_Pager_dots)
        if (runnableBanner != null) handlerBanner.removeCallbacks(runnableBanner!!)
        startAutoSliderBanner(adapterBanner.itemCount,holder.item_product_view_pager)
    }

    private fun startAutoSliderBanner(count: Int, itemProductViewPager: ViewPager2) {
        runnableBanner = Runnable {
            var pos: Int =itemProductViewPager.currentItem
            pos += 1
            if (pos >= count) pos = 0
            itemProductViewPager.currentItem = pos
            handlerBanner.postDelayed(runnableBanner!!, 3000)
        }
        handlerBanner.postDelayed(runnableBanner!!, 3000)
    }

    private var bannerPageChangeCallback: ViewPager2.OnPageChangeCallback = object :
        ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
        }
        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
        override fun onPageScrollStateChanged(arg0: Int) {}
    }
    private fun bottomProgressDotsBanner(current_index: Int,itemProductViewPagerDots: android.widget.LinearLayout) {
        val dots = arrayOfNulls<ImageView>(arrayList[0].images.size)
        itemProductViewPagerDots.removeAllViews()
        for (i in dots.indices) {
            dots[i] = ImageView(context)
            val widthHeight = 15
            val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams(widthHeight, widthHeight))
            params.setMargins(10, 10, 10, 10)
            dots[i]!!.layoutParams = params
            Glide.with(context).load(R.drawable.no_image).into(dots[i]!!)
            dots[i]!!.adjustViewBounds = true
            itemProductViewPagerDots.addView(dots[i])
        }
        if (dots.isNotEmpty()) {
            Glide.with(context).load(R.drawable.no_image).into(dots[current_index]!!)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var item_product_text_title: TextView
        var item_product_text_price: TextView
        var item_product_text_rating: TextView
        var item_product_text_ratingBar: RatingBar
        var item_product_view_pager: ViewPager2
        var item_product_view_Pager_dots:LinearLayout

        init {
            item_product_text_title = itemView.findViewById(R.id.item_title)
            item_product_text_price = itemView.findViewById(R.id.item_price)
            item_product_text_rating = itemView.findViewById(R.id.item_rating)
            item_product_text_ratingBar = itemView.findViewById(R.id.item_rating_bar)
            item_product_view_pager = itemView.findViewById(R.id.vertical_only)
            item_product_view_Pager_dots = itemView.findViewById(R.id.layout_dots)
        }
    }
}