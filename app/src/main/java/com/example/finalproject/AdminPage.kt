package com.example.finalproject

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class AdminPage : AppCompatActivity(), AdminCustomAdapter.OnEditClickListener, AdminCustomAdapter.OnDeleteClickListener  {
    private lateinit var adapter: AdminCustomAdapter
    private var productList = mutableListOf<ProductsViewModel>() // Initialize with your data
    private lateinit var productDatabaseHandler: DatabaseHandler
    private lateinit var selectedImageUri: Uri
    private val PICK_IMAGE_REQUEST = 1 // Define pick image request code
    private lateinit var productImageDisplay: ImageView
    private var uniqueIdCounter = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_page)

        productDatabaseHandler = DatabaseHandler(this)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        val productsFromDB = productDatabaseHandler.getAllProducts()

        val data = ArrayList<ProductsViewModel>()
        productList.addAll(productsFromDB) // Add fetched data to productList

        adapter = AdminCustomAdapter(
            productList,
            { position -> onEditClick(position) },
            { position -> onDeleteClick(position) }
        )

        recyclerView.adapter = adapter

        val openAddProduct = findViewById<ImageButton>(R.id.addproduct)
        openAddProduct.setOnClickListener {
            val message1 = "Add Product"
            showAddProducts(message1)
        }

        val openDialog = findViewById<Button>(R.id.Logoutbtn)
        openDialog.setOnClickListener {
            val message2 = "Are you sure you want to logout?"
            Logout(message2)

        }

    }

    override fun onEditClick(position: Int) {
        val selectedProduct = productList[position]
        showEditProducts("Update product", selectedProduct)
    }

    private fun showEditProducts(message: String, product: ProductsViewModel) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.admin_editproducts)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val productNameEditText = dialog.findViewById<EditText>(R.id.nameedittext)
        val originalPriceEditText = dialog.findViewById<EditText>(R.id.originalpriceedittext)
        val percentageOffEditText = dialog.findViewById<EditText>(R.id.percentageoffedittext)
        productImageDisplay = dialog.findViewById(R.id.productImageDisplay)
        val btnChooseImage = dialog.findViewById<Button>(R.id.btnChooseImage)
        val saveButton = dialog.findViewById<Button>(R.id.saveadd)
        val cancelButton = dialog.findViewById<Button>(R.id.cancel)

        productNameEditText.setText(product.name)
        originalPriceEditText.setText(product.origPrice.toString())
        percentageOffEditText.setText(product.percentageoff.toString())

        // Set the image using Glide or any other image loading library
        Glide.with(this)
            .load(product.image) // Assuming 'image' is a Uri field
            .into(productImageDisplay)

        btnChooseImage.setOnClickListener {
            openImageChooserForEdit()
        }

        saveButton.text = "Update"

        saveButton.setOnClickListener {
            val editedProductName = productNameEditText.text.toString()
            val editedOriginalPrice = originalPriceEditText.text.toString().toDoubleOrNull() ?: 0.0
            val editedPercentageOff = percentageOffEditText.text.toString().toIntOrNull() ?: 0
            val productId = product.id

            // Update the existing product details
            val updatedImage = if (::selectedImageUri.isInitialized) selectedImageUri else product.image

            val updatedProduct = ProductsViewModel(
                productId,
                editedPercentageOff,
                updatedImage,
                editedOriginalPrice,
                editedProductName
            )

            val rowsAffected = productDatabaseHandler.updateProduct(updatedProduct)
            if (rowsAffected > 0) {
                Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show()
                // Update the product list or perform any necessary updates
                adapter.updateItem(updatedProduct)
                adapter.notifyDataSetChanged() // Notify adapter of data change
            } else {
                Toast.makeText(this, "Failed to update product", Toast.LENGTH_SHORT).show()
            }

            dialog.dismiss()
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun openImageChooserForEdit() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }


    override fun onDeleteClick(position: Int) {
        val productName = productList[position].name
        showCustomDialogBox("Are you sure you want to delete $productName") { confirmed ->
            if (confirmed) {
                val productName = productList[position].name
                val rowsAffected = productDatabaseHandler.deleteProduct(productName)

                if (rowsAffected > 0) {
                    adapter.removeItem(position)
                    Toast.makeText(this, "Product deleted successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to delete product", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun showCustomDialogBox(message: String, callback: (Boolean) -> Unit) {
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
            callback.invoke(true) // Invoke the callback with true when confirmed
            dialog.dismiss()
        }
        btnNo.setOnClickListener {
            callback.invoke(false) // Invoke the callback with false when canceled
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun showAddProducts(message: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.admin_editproducts)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvMessage = dialog.findViewById<TextView>(R.id.editprod)
        val save = dialog.findViewById<Button>(R.id.saveadd)
        val cancel = dialog.findViewById<Button>(R.id.cancel)
        productImageDisplay = dialog.findViewById(R.id.productImageDisplay) // Initialize productImageDisplay here
        val btnChooseImage = dialog.findViewById<Button>(R.id.btnChooseImage)

        tvMessage.text = message
        save.text = "Add"

        // Listener for choosing an image
        btnChooseImage.setOnClickListener {
            openFileChooser()
        }

        save.setOnClickListener {

            val productNameEditText = dialog.findViewById<EditText>(R.id.nameedittext)
            val originalPriceEditText = dialog.findViewById<EditText>(R.id.originalpriceedittext)
            val percentageOffEditText = dialog.findViewById<EditText>(R.id.percentageoffedittext)

            val percentageOff = percentageOffEditText.text.toString().toIntOrNull() ?: 0
            val origPrice = originalPriceEditText.text.toString().toDoubleOrNull() ?: 0.0
            val productName = productNameEditText.text.toString()

            if (::selectedImageUri.isInitialized) {
                val imageUriString = selectedImageUri.toString()

                // Assuming you have a mechanism to generate a unique ID (e.g., from the database or other logic)
                val generatedId = generateUniqueId() // Replace this with your logic to generate ID

                // Create a ProductsViewModel instance with collected details and the generated ID
                val newProduct = ProductsViewModel(generatedId, percentageOff, Uri.parse(imageUriString), origPrice, productName)

                // Add the new product to the database
                val success = productDatabaseHandler.addProduct(newProduct)
                // Update the adapter's dataset and notify of the change
                adapter.addItem(newProduct)
                adapter.notifyDataSetChanged()

                if (success > -1) {
                    Toast.makeText(this, "Product Added!", Toast.LENGTH_LONG).show()
                    // Refresh the displayed products or perform any necessary updates
                } else {
                    Toast.makeText(this, "Failed to add product!", Toast.LENGTH_LONG).show()
                }
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please select an image first!", Toast.LENGTH_LONG).show()
            }
        }
        cancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun generateUniqueId(): Int {
        // Replace this with your logic to generate a unique ID (e.g., from the database or other method)
        return uniqueIdCounter++
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val selectedImage: Uri = data.data!!

            // Set the selected image in the ImageView
            productImageDisplay.setImageURI(selectedImage)

            // Assign the selected image URI to a variable to use it later when saving the product
            selectedImageUri = selectedImage
        }
    }




    private fun Logout(message: String) {
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
            Toast.makeText(this, "Logged Out as Admin", Toast.LENGTH_LONG).show()
            val i = Intent(this, LoginPage::class.java)
            startActivity(i)
            dialog.dismiss()
            finish()
        }
        btnNo.setOnClickListener {
            dialog.dismiss()
        }


        dialog.show()
    }






}