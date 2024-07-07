package com.example.efslha.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.efslha.R

class Feedback : AppCompatActivity() {
    lateinit var nametext: EditText
    lateinit var emailtext: EditText
    lateinit var subject: EditText
    lateinit var des: EditText
    lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        nametext = findViewById(R.id.nametext)
        emailtext = findViewById(R.id.emailtext)
        subject = findViewById(R.id.subject)
        des = findViewById(R.id.des)
        button = findViewById(R.id.button)

        button.setOnClickListener {
            val name = nametext.text.toString().trim()
            val email = emailtext.text.toString().trim()
            val subjectText = subject.text.toString().trim()
            val description = des.text.toString().trim()
            if (name.isEmpty() || email.isEmpty() || subjectText.isEmpty() || description.isEmpty()) {
                Toast.makeText(this@Feedback, "Please enter all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val emailText = """
        Name: $name
        Email: $email
        Subject: $subjectText
        Description: $description
    """.trimIndent()

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf("efslhaproject@gmail.com")) // recipient
                putExtra(Intent.EXTRA_SUBJECT, subjectText)
                putExtra(Intent.EXTRA_TEXT, emailText)
            }

            try {
                startActivity(Intent.createChooser(intent, "Send email"))
            } catch (e: Exception) {
                Toast.makeText(this@Feedback, "Failed to send email", Toast.LENGTH_SHORT).show()
            }
        }


    }
}

