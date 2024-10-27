package com.example.finalproject

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri

class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 7
        private const val DATABASE_NAME = "CombinedDatabase"
        private const val USERS_TABLE = "UserTable"
        private const val PRODUCTS_TABLE = "ProductTable"
        private const val KEY_ID = "id"
        private const val KEY_PERCENTAGE_OFF = "PercentageOff"
        private const val KEY_IMAGE_URI = "ImageUri"
        private const val KEY_ORIG_PRICE = "OrigPrice"
        private const val KEY_NAME = "Name"
        private const val KEY_USERNAME = "Username"
        private const val KEY_FNAME = "FirstName"
        private const val KEY_SURNAME = "Surname"
        private const val KEY_PASSWORD = "Password"

        private const val CART_TABLE = "CartTable"
        private const val KEY_USER_ID = "UserId"
        private const val KEY_PRODUCT_ID = "ProductId"
        private const val KEY_QUANTITY = "Quantity"
        private const val KEY_CART_IMAGE_URI = "ImageUriCart"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_USERS_TABLE = "CREATE TABLE $USERS_TABLE (" +
                "$KEY_USERNAME TEXT PRIMARY KEY," +
                "$KEY_FNAME TEXT," +
                "$KEY_SURNAME TEXT," +
                "$KEY_PASSWORD TEXT)"

        val CREATE_PRODUCTS_TABLE = "CREATE TABLE $PRODUCTS_TABLE (" +
                "$KEY_ID INTEGER PRIMARY KEY," +
                "$KEY_PERCENTAGE_OFF INTEGER," +
                "$KEY_IMAGE_URI TEXT," +
                "$KEY_ORIG_PRICE REAL," +
                "$KEY_NAME TEXT)"

        val CREATE_CART_TABLE = "CREATE TABLE $CART_TABLE (" +
                "$KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$KEY_USER_ID TEXT," +
                "$KEY_PRODUCT_ID INTEGER," +
                "$KEY_QUANTITY INTEGER DEFAULT 1," +
                "$KEY_CART_IMAGE_URI TEXT," +
                "FOREIGN KEY ($KEY_USER_ID) REFERENCES $USERS_TABLE($KEY_USERNAME)," +
                "FOREIGN KEY ($KEY_PRODUCT_ID) REFERENCES $PRODUCTS_TABLE($KEY_ID))"


        db?.execSQL(CREATE_USERS_TABLE)
        db?.execSQL(CREATE_PRODUCTS_TABLE)
        db?.execSQL(CREATE_CART_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $USERS_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $PRODUCTS_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $CART_TABLE")
        onCreate(db)
    }

    // User-related functions

    fun addUser(user: UsersViewModel): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(DatabaseHandler.KEY_USERNAME, user.username)
        contentValues.put(DatabaseHandler.KEY_FNAME, user.firstname)
        contentValues.put(DatabaseHandler.KEY_SURNAME, user.surname)
        contentValues.put(DatabaseHandler.KEY_PASSWORD, user.password)

        val success = db.insert(DatabaseHandler.USERS_TABLE, null, contentValues)
        db.close()

        return success
    }

    fun verifyUser(username: String, password: String): Boolean {
        val db = readableDatabase
        val query = "SELECT * FROM ${DatabaseHandler.USERS_TABLE} WHERE ${DatabaseHandler.KEY_USERNAME} = ? AND ${DatabaseHandler.KEY_PASSWORD} = ?"
        val cursor = db.rawQuery(query, arrayOf(username, password))
        val result = cursor.count > 0
        cursor.close()
        db.close()
        return result
    }

    fun isDatabaseExists(): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM ${DatabaseHandler.USERS_TABLE}"
        val cursor = db.rawQuery(query, null)
        val exists = cursor != null && cursor.count > 0
        cursor?.close()
        return exists
    }

    /*Eto den pala di ko alam kung gagamitin kasi panibago nanaman crud nanaman to tas lalabas sa admin side ang focus nalang naten is yung sa deals*/
    fun updateUser(user: UsersViewModel): Int
    {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(DatabaseHandler.KEY_USERNAME, user.username)
        contentValues.put(DatabaseHandler.KEY_FNAME, user.firstname)
        contentValues.put(DatabaseHandler.KEY_SURNAME, user.surname)
        contentValues.put(DatabaseHandler.KEY_PASSWORD, user.password)
        val success = db.update(DatabaseHandler.USERS_TABLE, contentValues, "${DatabaseHandler.KEY_USERNAME} = ${user.username}", null)
        db.close()
        return success
    }

    /*Di ko sure kung gagamitin as is ko nalang muna to */
    fun deleteUser(user: UsersViewModel): Int
    {
        val db = this.writableDatabase
        val success = db.delete(DatabaseHandler.USERS_TABLE, "${DatabaseHandler.KEY_USERNAME} = ${user.username}", null)
        db.close()

        return success
    }

    @SuppressLint("Range")
    fun getUserDetailsByUsername(username: String): UsersViewModel? {
        val db = this.readableDatabase
        val query = "SELECT * FROM ${DatabaseHandler.USERS_TABLE} WHERE ${DatabaseHandler.KEY_USERNAME}=?"
        val cursor = db.rawQuery(query, arrayOf(username))

        var userDetails: UsersViewModel? = null
        if (cursor.moveToFirst()) {
            userDetails = UsersViewModel(
                cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_USERNAME)),
                cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_FNAME)),
                cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SURNAME)),
                cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_PASSWORD))
            )
        }
        cursor.close()
        return userDetails
    }

    // Product-related functions

    fun addProduct(product: ProductsViewModel): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(DatabaseHandler.KEY_PERCENTAGE_OFF, product.percentageoff)
        contentValues.put(DatabaseHandler.KEY_IMAGE_URI, product.image.toString())
        contentValues.put(DatabaseHandler.KEY_ORIG_PRICE, product.origPrice)
        contentValues.put(DatabaseHandler.KEY_NAME, product.name)

        val success = db.insert(DatabaseHandler.PRODUCTS_TABLE, null, contentValues)
        db.close()
        return success
    }

    @SuppressLint("Range")
    fun getAllProducts(): ArrayList<ProductsViewModel> {
        val productList = ArrayList<ProductsViewModel>()
        val selectQuery = "SELECT * FROM ${DatabaseHandler.PRODUCTS_TABLE}"
        val db = this.readableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_ID))
                val percentageOff = cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_PERCENTAGE_OFF))
                val imageUriString = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_IMAGE_URI))
                val origPrice = cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_ORIG_PRICE))
                val name = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_NAME))

                // Convert the stored string back to Uri
                val imageUri = Uri.parse(imageUriString)

                val product = ProductsViewModel(id, percentageOff, imageUri, origPrice, name)
                productList.add(product)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return productList
    }

    fun deleteProduct(productName: String): Int {
        val db = this.writableDatabase
        val selection = "${DatabaseHandler.KEY_NAME} = ?"
        val selectionArgs = arrayOf(productName)
        val success = db.delete(DatabaseHandler.PRODUCTS_TABLE, selection, selectionArgs)
        db.close()
        return success
    }

    fun updateProduct(product: ProductsViewModel): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(DatabaseHandler.KEY_PERCENTAGE_OFF, product.percentageoff)
        contentValues.put(DatabaseHandler.KEY_IMAGE_URI, product.image.toString())
        contentValues.put(DatabaseHandler.KEY_ORIG_PRICE, product.origPrice)
        contentValues.put(DatabaseHandler.KEY_NAME, product.name)

        val selection = "${DatabaseHandler.KEY_ID} = ?" // Use the primary key (id) as the reference for updating
        val selectionArgs = arrayOf(product.id.toString()) // Use the existing product id as a reference

        val success = db.update(DatabaseHandler.PRODUCTS_TABLE, contentValues, selection, selectionArgs)
        db.close()
        return success
    }
    @SuppressLint("Range")
    fun getProductById(productId: Int): ProductsViewModel? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $PRODUCTS_TABLE WHERE $KEY_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(productId.toString()))

        var product: ProductsViewModel? = null

        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
            val percentageOff = cursor.getInt(cursor.getColumnIndex(KEY_PERCENTAGE_OFF))
            val imageUriString = cursor.getString(cursor.getColumnIndex(KEY_IMAGE_URI))
            val origPrice = cursor.getDouble(cursor.getColumnIndex(KEY_ORIG_PRICE))
            val name = cursor.getString(cursor.getColumnIndex(KEY_NAME))

            // Convert the stored string back to Uri
            val imageUri = Uri.parse(imageUriString)

            product = ProductsViewModel(id, percentageOff, imageUri, origPrice, name)
        }

        cursor.close()
        db.close()
        return product
    }


    @SuppressLint("Range")
    fun addToCart(user: UsersViewModel, cartItem: CartViewModel): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_USER_ID, user.username)
        contentValues.put(KEY_PRODUCT_ID, cartItem.product.id)
        contentValues.put(KEY_QUANTITY, cartItem.quantity)
        contentValues.put(KEY_CART_IMAGE_URI, cartItem.product.image.toString()) // Assuming product.image is a Uri

        // Check if the item with the same ID exists for the user in the cart
        val query = "SELECT $KEY_QUANTITY FROM $CART_TABLE WHERE $KEY_USER_ID = ? AND $KEY_PRODUCT_ID = ?"
        val selectionArgs = arrayOf(user.username, cartItem.product.id.toString())
        val cursor = db.rawQuery(query, selectionArgs)

        return if (cursor.moveToFirst()) {
            // Item already exists in the cart, update the quantity
            val currentQuantity = cursor.getInt(cursor.getColumnIndex(KEY_QUANTITY))
            val updatedQuantity = currentQuantity + cartItem.quantity

            val updateContentValues = ContentValues()
            updateContentValues.put(KEY_QUANTITY, updatedQuantity)

            // Update the quantity for the existing cart item
            val affectedRows = db.update(
                CART_TABLE,
                updateContentValues,
                "$KEY_USER_ID = ? AND $KEY_PRODUCT_ID = ?",
                arrayOf(user.username, cartItem.product.id.toString())
            )

            cursor.close()
            affectedRows.toLong()
        } else {
            // Item does not exist in the cart, insert a new row
            val success = db.insert(CART_TABLE, null, contentValues)
            cursor.close()
            db.close()
            success
        }
    }


    @SuppressLint("Range")
    fun getCartItemsForUser(userId: String): ArrayList<CartViewModel> {
        val cartItems = ArrayList<CartViewModel>()
        val db = this.readableDatabase

        val query = "SELECT * FROM $CART_TABLE WHERE $KEY_USER_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(userId))

        if (cursor.moveToFirst()) {
            do {
                val productId = cursor.getInt(cursor.getColumnIndex(KEY_PRODUCT_ID))
                val quantity = cursor.getInt(cursor.getColumnIndex(KEY_QUANTITY))
                val imageUriString = cursor.getString(cursor.getColumnIndex(KEY_CART_IMAGE_URI))

                // Fetch product details from PRODUCTS_TABLE based on productId
                val product = getProductById(productId)

                // Perform null check for product
                product?.let {
                    val imageUri = Uri.parse(imageUriString)
                    val cartItem = CartViewModel(userId, it, quantity, imageUri)
                    cartItems.add(cartItem)
                }
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return cartItems
    }


    fun removeFromCart(cartItem: CartViewModel): Int {
        val db = this.writableDatabase
        val whereClause = "$KEY_USER_ID = ? AND $KEY_PRODUCT_ID = ?" // Define your condition here
        val whereArgs = arrayOf(cartItem.userId, cartItem.product.id.toString()) // Use the appropriate identifiers
        val success = db.delete(CART_TABLE, whereClause, whereArgs)
        db.close()
        return success
    }


    fun updateCartItemQuantity(userId: String, productId: Int, newQuantity: Int): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_QUANTITY, newQuantity)

        val whereClause = "$KEY_USER_ID = ? AND $KEY_PRODUCT_ID = ?"
        val whereArgs = arrayOf(userId, productId.toString())

        val success = db.update(CART_TABLE, contentValues, whereClause, whereArgs)
        db.close()
        return success
    }



    fun clearCartTable() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $CART_TABLE")
        db.close()
    }



}
