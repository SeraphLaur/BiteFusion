package com.example.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class LandingPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.landing_page)
    }
    fun toLoginPage(view: View){
        val i = Intent(this, LoginPage::class.java)
        startActivity(i)
    }

    fun toRegisterPage(view: View){
        val i = Intent(this, RegisterPage::class.java)
        startActivity(i)
    }

}