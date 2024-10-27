package com.example.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class RegisterPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_page)
    }


    fun toHomePageButton(view: View){

        val firstnametext = findViewById<EditText>(R.id.firstnametext)
        val surnametext = findViewById<EditText>(R.id.surnametext)
        val usernametext = findViewById<EditText>(R.id.usernametext)
        val passwordtext = findViewById<EditText>(R.id.passwordtext)
        val confirmpasswordtext = findViewById<EditText>(R.id.confirmpasswordtext)
        val verificationResultTextView = findViewById<TextView>(R.id.verification)

        val firstname = firstnametext.text.toString()
        val surname = surnametext.text.toString()
        val username = usernametext.text.toString()
        val password = passwordtext.text.toString()
        val confirmpassword = confirmpasswordtext.text.toString()
        val databaseHandlerUser = DatabaseHandler(this)

        if (firstname.isNotEmpty() && surname.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty() && confirmpassword.isNotEmpty()) {

            if (password != confirmpassword){
                Toast.makeText(this, "Passwords do not match, try again!", Toast.LENGTH_LONG).show()
            } else {
                verificationResultTextView.text = ""
                Toast.makeText(this, "Register Successful!", Toast.LENGTH_LONG).show()
                databaseHandlerUser.addUser(UsersViewModel(username, firstname, surname, password))
                val i = Intent(this, LoginPage::class.java)
                startActivity(i)
            }

        } else {
            verificationResultTextView.text = "Please fill out all the missing fields"
        }




    }

    fun toHomePage(view: View){
        val i = Intent(this, LandingPage::class.java)
        startActivity(i)
    }
}