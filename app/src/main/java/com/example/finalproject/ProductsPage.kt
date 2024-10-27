    package com.example.finalproject

    import android.app.Dialog
    import android.content.Context
    import android.content.Intent
    import android.content.SharedPreferences
    import android.graphics.Color
    import android.graphics.drawable.ColorDrawable
    import android.net.Uri
    import androidx.appcompat.app.AppCompatActivity
    import android.os.Bundle
    import android.view.View
    import android.view.Window
    import android.widget.Button
    import android.widget.TextView
    import android.widget.Toast
    import androidx.recyclerview.widget.GridLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import com.example.myfirstapp3itg.ProductsCustomAdapter

    class ProductsPage : AppCompatActivity() {
        private lateinit var sharedPreferences: SharedPreferences
        private lateinit var productDatabaseHandler: DatabaseHandler
        private lateinit var selectedProduct: ProductsViewModel
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.products_page)

            sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            productDatabaseHandler = DatabaseHandler(this)

            val textviewUsername = findViewById<TextView>(R.id.welcome)
            val username = sharedPreferences.getString("username", "")
            textviewUsername.text = "Hello, $username!"

            val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
            recyclerView.layoutManager = GridLayoutManager(this, 2)

            // Fetch products from the database
            val productsFromDB = productDatabaseHandler.getAllProducts()

            val adapter = ProductsCustomAdapter(productsFromDB)

            adapter.setOnItemClickListener(object : ProductsCustomAdapter.OnItemClickListener {
                override fun onItemClick(item: ProductsViewModel) {
                    selectedProduct = item
                    showCustomDialogBox("Do you want to add to cart?")
                }
            })

            recyclerView.adapter = adapter
        }


        fun toProfilePage(view: View){
            val i = Intent(this, ProfilePage::class.java)
            startActivity(i)
        }

        fun toCartPage(view: View){
            val i = Intent(this, CartPage::class.java)
            startActivity(i)
        }

        private fun showCustomDialogBox(message: String) {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.custom_dialog)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val tvMessage = dialog.findViewById<TextView>(R.id.tvMessage)
            val btnYes = dialog.findViewById<Button>(R.id.btnYes)
            val btnNo = dialog.findViewById<Button>(R.id.btnNo)

            tvMessage.text = message
            btnYes.setOnClickListener {
                val username = sharedPreferences.getString("username", "") ?: ""
                val user = productDatabaseHandler.getUserDetailsByUsername(username)

                if (user != null && selectedProduct != null) {
                    val imageUriString = selectedProduct.image.toString() // Get image Uri string from selected product

                    val imageUri = Uri.parse(imageUriString) // Convert the string to Uri
                    val cartItem = CartViewModel(user.username, selectedProduct, 1, imageUri) // Create CartViewModel with image Uri

                    val success = productDatabaseHandler.addToCart(user, cartItem)

                    if (success > -1) {
                        Toast.makeText(this, "Added to Cart", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Failed to add to Cart", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, "User details or selected product not found", Toast.LENGTH_LONG).show()
                }

                dialog.dismiss()
            }



            btnNo.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }
