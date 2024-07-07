package com.example.efslha

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.efslha.ui.main.Feedback

class donedeal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donedeal)

    }

    fun help(view: View) {
        val a=Intent(this@donedeal,HelpActivity::class.java)
        startActivity(a)
    }
    fun collections(view: View) {
        val a=Intent(this@donedeal,CollectorCollections::class.java)
        startActivity(a)
    }
    fun msgcenter(view: View) {
        val a=Intent(this@donedeal,Feedback::class.java)
        startActivity(a)
    }
}