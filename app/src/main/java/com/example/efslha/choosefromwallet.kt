package com.example.efslha

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class choosefromwallet : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choosefromwallet)

    }

    fun cashredeem(view: View) {
        val a =Intent(this@choosefromwallet,paypal::class.java)
        startActivity(a)

    }

    fun offers(view: View) {
        val a =Intent(this@choosefromwallet,couponsmanger::class.java)
        startActivity(a)
    }
}