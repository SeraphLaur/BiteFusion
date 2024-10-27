package com.example.myfirstapp3itg

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.finalproject.CartViewModel
import com.example.finalproject.DatabaseHandler
import com.example.finalproject.R
import java.math.BigDecimal

class CartCustomAdapter(
    private val itemList: MutableList<CartViewModel>,
    private val listener: TotalUpdateListener,
    private val dbHandler: DatabaseHandler // Pass the instance of DatabaseHandler
) : RecyclerView.Adapter<CartCustomAdapter.ViewHolder>() {

    interface TotalUpdateListener {
        fun onTotalUpdated(total: Double)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_cardview, parent, false)
        return ViewHolder(view)
    }

    private fun notifyTotalUpdated() {
        listener.onTotalUpdated(calculateTotal())
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cartItem = itemList[position]

        val discountedPrice = calculateDiscountedPrice(cartItem)

        Glide.with(holder.itemView.context)
            .load(cartItem.image) // Assuming 'image' is a Uri field
            .into(holder.imageview)
        holder.name.text = cartItem.product.name
        holder.discountedPrice.text = "₱${String.format("%.2f", discountedPrice)}"

        holder.quantityTextView.text = cartItem.quantity.toString()

        holder.plusButton.setOnClickListener {
            cartItem.quantity++
            holder.quantityTextView.text = cartItem.quantity.toString()
            notifyTotalUpdated()
            dbHandler.updateCartItemQuantity(cartItem.userId, cartItem.product.id, cartItem.quantity) // Update the quantity in the database
        }

        holder.minusButton.setOnClickListener {
            if (cartItem.quantity > 0) {
                cartItem.quantity--
                holder.quantityTextView.text = cartItem.quantity.toString()

                if (cartItem.quantity == 0) {
                    dbHandler.removeFromCart(cartItem) // Pass the cartItem to removeFromCart function
                    removeItem(holder.adapterPosition)
                } else {
                    dbHandler.updateCartItemQuantity(cartItem.userId, cartItem.product.id, cartItem.quantity) // Update the quantity in the database
                }
                notifyTotalUpdated()
            }
        }



    }

    private fun calculateDiscountedPrice(cartItem: CartViewModel): Double {
        val numericValueofOrigPrice = cartItem.product.origPrice.toDouble()
        val percentageOff = cartItem.product.percentageoff.toString().toDouble()

        val calculatedDiscount = (percentageOff / 100) * numericValueofOrigPrice
        return numericValueofOrigPrice - calculatedDiscount
    }



    fun calculateTotal(): Double {
        var totalAmount = BigDecimal.ZERO
        for (item in itemList) {
            val numericValueofPercentage = item.product.percentageoff.toString().split("% off")[0].trim().toDouble()
            val numericValueofOrigPrice = item.product.origPrice.toDouble()
            val calculatedDiscount = (numericValueofPercentage / 100) * numericValueofOrigPrice
            val discountedPrice = numericValueofOrigPrice - calculatedDiscount

            val price = BigDecimal(discountedPrice.toString())
            val quantity = BigDecimal(item.quantity.toString())
            val totalPriceForItem = price.multiply(quantity)
            totalAmount = totalAmount.add(totalPriceForItem)
        }
        return totalAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
    }

    fun getCartData(): String {
        val cartDataStringBuilder = StringBuilder()

        for (item in itemList) {
            val numericValueofPercentage = item.product.percentageoff.toString().split("% off")[0].trim().toDouble()
            val numericValueofOrigPrice = item.product.origPrice.toDouble()
            val calculatedDiscount = (numericValueofPercentage / 100) * numericValueofOrigPrice
            val discountedPrice = numericValueofOrigPrice - calculatedDiscount

            cartDataStringBuilder.append("Item: ${item.product.name}, Quantity: ${item.quantity}, Price: ₱${String.format("%.2f", discountedPrice)} Pesos\n")
        }

        return cartDataStringBuilder.toString()
    }



    private fun removeItem(position: Int) {
        itemList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val discountedPrice: TextView = itemView.findViewById(R.id.discountedPrice)
        val imageview: ImageView = itemView.findViewById(R.id.image)
        val plusButton: Button = itemView.findViewById(R.id.plusButton)
        val minusButton: Button = itemView.findViewById(R.id.minusButton)
        val quantityTextView: TextView = itemView.findViewById(R.id.quantityTextView)
    }
}
