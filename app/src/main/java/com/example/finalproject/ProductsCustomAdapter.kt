package com.example.myfirstapp3itg

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.finalproject.ProductsViewModel
import com.example.finalproject.R

class ProductsCustomAdapter(private val mList: List<ProductsViewModel>) : RecyclerView.Adapter<ProductsCustomAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.products_cardview, parent, false)
        return ViewHolder(view)
    }

    //to make the layout clickable
    interface OnItemClickListener {
        fun onItemClick(item: ProductsViewModel)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = mList[position]

        val numericValueofPercentage = product.percentageoff.toDouble()
        val numericValueofOrigPrice = product.origPrice

        val calculatedDiscount = (numericValueofPercentage / 100) * numericValueofOrigPrice
        val discountedPrice = numericValueofOrigPrice - calculatedDiscount

        val percentageInteger = numericValueofPercentage.toInt()

        holder.percentageoff.text = "$percentageInteger% off"
        holder.discountedPrice.text = "₱${String.format("%.2f", discountedPrice)}"
        holder.origPrice.text = "₱${String.format("%.2f", numericValueofOrigPrice)}"
        Glide.with(holder.itemView.context)
            .load(product.image) // Using the 'image' field directly from ProductsViewModel
            .into(holder.imageview)
        holder.name.text = product.name

        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(product)
        }
    }


    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView){
        val percentageoff: TextView = itemView.findViewById(R.id.percentageoff)
        val discountedPrice: TextView = itemView.findViewById(R.id.discountedPrice)
        val origPrice: TextView = itemView.findViewById(R.id.origPrice)
        val imageview: ImageView = itemView.findViewById(R.id.imageview)
        val name: TextView = itemView.findViewById(R.id.name)
    }
}