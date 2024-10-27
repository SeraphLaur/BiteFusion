package com.example.finalproject

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.EnumMap

class QRCodePage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qrcode_page)

        val qrCodeImageView = findViewById<ImageView>(R.id.qrcodeimageview)
        val totalBillTextView = findViewById<TextView>(R.id.totalqr) // Get the TextView reference

        val cartData = intent.getStringExtra("CART_DATA") ?: ""
        val totalBill = intent.getDoubleExtra("TOTAL_BILL", 0.0)

        val completeData = "$cartData \nTotal Bill: $totalBill Pesos" // Combine both data

        // Set totalBill value to the TextView
        totalBillTextView.text = "₱$totalBill"

        // Generate QR Code
        val bitmap = generateQRCode(completeData)
        qrCodeImageView.setImageBitmap(bitmap)
    }

    private fun generateQRCode(data: String): Bitmap? {
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H // Error correction level

        try {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512, hints)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
                }
            }
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun toDealsPage(view: View){
        val databaseHandler = DatabaseHandler(this)
        databaseHandler.clearCartTable()
        val i = Intent(this, ProductsPage::class.java)
        startActivity(i)
    }
}
