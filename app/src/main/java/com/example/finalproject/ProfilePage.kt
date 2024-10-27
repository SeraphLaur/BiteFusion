package com.example.finalproject

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class ProfilePage : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_page)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val textViewUsername: TextView = findViewById(R.id.usernametextview)
        val textViewFirstName: TextView = findViewById(R.id.firstnametextview)
        val logoutSurname: TextView = findViewById(R.id.surnametextview)

        val firstName = sharedPreferences.getString("username", "")
        val lastName = sharedPreferences.getString("firstName", "")
        val email = sharedPreferences.getString("lastName", "")

        textViewUsername.text = "$firstName"
        textViewFirstName.text = "$lastName"
        logoutSurname.text = "$email"

        val openDialog = findViewById<Button>(R.id.Logoutbtn)
        openDialog.setOnClickListener {
            val message = "Are you sure you want to logout?"
            showCustomDialogBox(message)

        }

    }
    fun toDealsPage(view: View){
        val i = Intent(this, ProductsPage::class.java)
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
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            Toast.makeText(this, "Successfully Logged Out!", Toast.LENGTH_LONG).show()
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