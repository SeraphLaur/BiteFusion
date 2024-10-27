package com.example.finalproject

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class LoginPage : AppCompatActivity() {
    private lateinit var dbHandler: DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

        dbHandler = DatabaseHandler(this)

    }
    fun toHomePage(view: View){

        val i = Intent(this, LandingPage::class.java)
        startActivity(i)

    }


    fun toDealsPage(view: View){

        val usernameEditText = findViewById<EditText>(R.id.usernameEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val verificationResultTextView = findViewById<TextView>(R.id.verification)

        val username = usernameEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (username.isNotEmpty() && password.isNotEmpty()) {

            val loginSuccessful = dbHandler.verifyUser(username, password)
            if (dbHandler.isDatabaseExists()) {
                if(username=="admin" && password=="123"){
                    verificationResultTextView.text = ""
                    Toast.makeText(this, "Logged in as Admin", Toast.LENGTH_SHORT).show()
                    val i = Intent(this, AdminPage::class.java)
                    startActivity(i)
                }
                else if (loginSuccessful) {
                    val userDetails = dbHandler.getUserDetailsByUsername(username)
                    userDetails?.let {
                        val sharedPreferences =
                            getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("username", userDetails.username)
                        editor.putString("firstName", userDetails.firstname)
                        editor.putString("lastName", userDetails.surname)
                        editor.apply()
                    }
                    verificationResultTextView.text = ""

                    Toast.makeText(this, "Logged in successfully!", Toast.LENGTH_LONG).show()
                    val i = Intent(this, ProductsPage::class.java)
                    startActivity(i)
                } else {
                    verificationResultTextView.text = "Account not found!"
                }
            } else {
                verificationResultTextView.text = "No user account found. Please register first."
                val i = Intent(this, RegisterPage::class.java)
                startActivity(i)
            }

        } else {
            verificationResultTextView.text = "Please enter both username and password"
        }


    }
}