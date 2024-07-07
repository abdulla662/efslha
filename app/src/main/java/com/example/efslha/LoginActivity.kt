package com.example.efslha

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.example.efslha.databinding.ActivityLoginBinding

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.getField


class LoginActivity : AppCompatActivity() {
    private lateinit var firbaseauth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sp: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var isPasswordVisible: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        sp = getSharedPreferences(Constants.USER_DATA, MODE_PRIVATE)
        editor = sp.edit()

        binding.apply {
            binding.eyeToggle.setOnClickListener {
                isPasswordVisible = !isPasswordVisible
                if (isPasswordVisible) {
                    password.inputType = InputType.TYPE_CLASS_TEXT
                    binding.eyeToggle.setImageResource(R.drawable.eyeopen)
                } else {
                    password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    binding.eyeToggle.setImageResource(R.drawable.eyeclosed)
                }
                password.setSelection(password.text.length)
            }
            setContentView(root)
            firbaseauth = FirebaseAuth.getInstance()
            firestore = FirebaseFirestore.getInstance()
            login1.setOnClickListener {
                val name1 = name1.text.toString()
                val password = password.text.toString()
                if (name1.isNotEmpty() && password.isNotEmpty()) {

                    firbaseauth.signInWithEmailAndPassword(name1, password).addOnCompleteListener {
                        if (it.isSuccessful && it.result != null) {
                            val currentUser = firbaseauth.currentUser
                            if (currentUser != null) {
                                val uid = currentUser.uid
                                firestore.collection(Constants.COLLECTORS_COLLECTION).document(uid)
                                    .get()
                                    .addOnCompleteListener { task ->

                                        val isCollector =
                                            if (!task.isSuccessful || task.result == null) false else task.result.getBoolean(
                                                "collector"
                                            ) ?: false

                                        val intent = Intent(
                                            this@LoginActivity,
                                            if (isCollector)
                                                CollectorLocation::class.java
                                            else userpage::class.java
                                        )
                                        if (!isCollector) {
                                            firestore.collection(Constants.SELLERS_COLLECTION)
                                                .document(currentUser.uid)
                                                .get().addOnSuccessListener { sellerData ->
                                                    if (sellerData.getString(Constants.SELLER_NAME_PATH) != null)
                                                        editor.putString(
                                                            Constants.SELLER_NAME_PATH,
                                                            sellerData.getString(Constants.SELLER_NAME_PATH)
                                                        )
                                                    editor.putBoolean(Constants.IS_COLLECTOR, false)
                                                    editor.apply()
                                                    startActivity(intent)
                                                    finish()
                                                }
                                        } else {
                                            editor.putBoolean(Constants.IS_COLLECTOR, true)
                                            editor.apply()
                                            startActivity(intent)
                                            finish()
                                        }
                                    }
                            } else {
                                Toast.makeText(
                                    this@LoginActivity,
                                    it.exception.toString(),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "Error: ${it.exception?.localizedMessage ?: "unknown error"}",
                                Toast.LENGTH_LONG
                            ).show()

                        }

                    }
                } else {
                    Toast.makeText(this@LoginActivity, "NO data", Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    fun forgotpass(view: View) {
          val a=Intent(this,forgotpassword::class.java)
        startActivity(a)
    }
}



