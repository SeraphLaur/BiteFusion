package com.example.finalproject

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class AdminCustomAdapter(private val mList: MutableList<ProductsViewModel>, private val onEditClickListener: (Int) -> Unit, private val onDeleteClickListener: (Int) -> Unit) : RecyclerView.Adapter<AdminCustomAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.admin_cardview, parent, false)
        return ViewHolder(view)
    }


    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ProductsViewModel = mList[position]

        val numericValueofPercentage = ProductsViewModel.percentageoff.toString().split("% off")[0].trim().toDouble()
        val numericValueofOrigPrice = ProductsViewModel.origPrice.toDouble()

        val calculatedDiscount = (numericValueofPercentage / 100) * numericValueofOrigPrice
        val discountedPrice = numericValueofOrigPrice - calculatedDiscount

        val percentageInteger = numericValueofPercentage.toInt()

        holder.percentageoff.text = "$percentageInteger% off"
        holder.discountedPrice.text = "₱${String.format("%.2f", discountedPrice)}"
        holder.origPrice.text = "₱${String.format("%.2f", numericValueofOrigPrice)}"
            Glide.with(holder.itemView.context)
                .load(ProductsViewModel.image) // Assuming 'image' is a Uri field
                .into(holder.imageview)

        holder.name.text = ProductsViewModel.name

        holder.editProduct.setOnClickListener {
            onEditClickListener.invoke(position)
        }

        holder.deleteProduct.setOnClickListener {
            onDeleteClickListener.invoke(position)
        }

    }

    // To automatically reflect changes made

    fun addItem(product: ProductsViewModel) {
        mList.add(product)
    }

    fun updateItem(product: ProductsViewModel) {
        val index = mList.indexOfFirst { it.id == product.id } // Assuming there is an 'id' field in ProductsViewModel
        if (index != -1) {
            mList[index] = product
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
        val editProduct: Button = itemView.findViewById(R.id.editproduct)
        val deleteProduct: Button = itemView.findViewById(R.id.deleteproduct)
    }

    fun removeItem(position: Int) {
        if (position in 0 until mList.size) {
            mList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, mList.size)
        }
    }

    interface OnEditClickListener {
        fun onEditClick(position: Int)
    }

    interface OnDeleteClickListener {
        fun onDeleteClick(position: Int)
    }



}