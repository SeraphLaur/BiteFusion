package com.example.finalproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp3itg.CartCustomAdapter

import com.bumptech.glide.Glide

class CartPage : AppCompatActivity(), CartCustomAdapter.TotalUpdateListener {

    private lateinit var adapter: CartCustomAdapter
    private lateinit var totalTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartItems: MutableList<CartViewModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cart_page)

        totalTextView = findViewById(R.id.total)
        recyclerView = findViewById(R.id.recyclerview2)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("username", "") ?: ""

        if (userId.isNotEmpty()) {
            // If userId is available, retrieve cart items
            val dbHandler = DatabaseHandler(this)
            cartItems = dbHandler.getCartItemsForUser(userId)

            if (cartItems.isNotEmpty()) {
                adapter = CartCustomAdapter(cartItems, this, dbHandler) // Pass dbHandler to the adapter
                recyclerView.adapter = adapter
                updateTotal()
            } else {
                // If cartItems is empty, display a message or handle the scenario accordingly
                Toast.makeText(this, "No items in the cart", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "User ID empty", Toast.LENGTH_SHORT).show()
            // Handle the scenario where user ID is empty or not available
            // This could include redirecting to a login/register page or handling the absence of user ID
        }

    }


    override fun onTotalUpdated(total: Double) {
        totalTextView.text = "₱$total"
    }

    private fun updateTotal() {
        val totalValue = adapter.calculateTotal()
        totalTextView.text = "₱$totalValue"
    }

    fun toProfilePage(view: View) {
        val i = Intent(this, ProfilePage::class.java)
        startActivity(i)
    }

    fun toDealsPage(view: View) {
        val i = Intent(this, ProductsPage::class.java)
        startActivity(i)
    }

    fun toQRCode(view: View) {
        val cartData = adapter.getCartData()
        val totalBill = adapter.calculateTotal()

        val intent = Intent(this, QRCodePage::class.java)
        intent.putExtra("CART_DATA", cartData)
        intent.putExtra("TOTAL_BILL", totalBill)
        startActivity(intent)
    }



}


