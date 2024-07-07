package com.example.efslha

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class HelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
    }

    fun collections(view: View) {
        val a = Intent(this,CollectorCollections::class.java)
        startActivity(a)
    }
    fun msgcenter(view: View) {
        val a = Intent(this,MessageCenterActivity::class.java)
        startActivity(a)
    }
    fun help(view: View){
        val a = Intent(this,HelpActivity::class.java)
        startActivity(a)
    }
}