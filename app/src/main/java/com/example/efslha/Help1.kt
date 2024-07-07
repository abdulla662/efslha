package com.example.efslha

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.efslha.ui.main.Feedback

class Help1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help1)

        }

    fun feedback(view: View) {
        val a=Intent(this@Help1,Feedback::class.java)
        startActivity(a)
    }

    fun collections(view: View) {
        val a=Intent(this@Help1,OrderHistory::class.java)
        startActivity(a)
    }
}
