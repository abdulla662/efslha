package com.example.efslha

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.VideoView
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {
    lateinit var videoView: VideoView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        val videoView = findViewById<VideoView>(R.id.videoView)
        val packageName = packageName
        val uri = Uri.parse("android.resource://$packageName/${R.raw.intro}")
        videoView.setVideoURI(uri)
        videoView.start()
        videoView.setOnCompletionListener {
            val a = Intent(this, LoginSignupActivity::class.java)
            startActivity(a)
            finish()



        }

    }
}