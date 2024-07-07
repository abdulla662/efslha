package com.example.efslha

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth

class forgotpassword : AppCompatActivity() {
    lateinit var sendemail:Button
    private lateinit var email:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotpassword)
          sendemail=findViewById(R.id.sendemail)
            email=findViewById(R.id.email)
        sendemail.setOnClickListener {
            val email=email.text.toString().trim()
            if (email.isEmpty()){
                Toast.makeText(this, "please enter an email", Toast.LENGTH_SHORT).show()
            }else{
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener {task ->
                     if (task.isSuccessful){
                         Toast.makeText(this, "the email sent successfully", Toast.LENGTH_SHORT).show()
                     }else {
                         Toast.makeText(this@forgotpassword, task.exception?.localizedMessage.toString(), Toast.LENGTH_SHORT).show()
                             }
                     }
                    }
            }
        }
                     }


