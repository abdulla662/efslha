package com.example.efslha

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.efslha.databinding.ActivitySellersignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SellerSignup : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivitySellersignupBinding
    private lateinit var sp: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var isPasswordVisible: Boolean = false
    private var isPasswordVisible1: Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySellersignupBinding.inflate(layoutInflater)
        setContentView(binding.root)  // This should be done before accessing views

        sp = getSharedPreferences(Constants.USER_DATA, MODE_PRIVATE)
        editor = sp.edit()
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.apply {
            eyeToggle.setOnClickListener {
                if (isPasswordVisible) {
                    pass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    eyeToggle.setImageResource(
                        R.drawable.eyeclosed)
                    isPasswordVisible = false
                    pass.setSelection(pass.text.length)
                } else {
                    pass.inputType = InputType.TYPE_CLASS_TEXT
                    eyeToggle.setImageResource(R.drawable.eyeopen)
                    isPasswordVisible = true
                    pass.setSelection(pass.text.length)
                }
            }
            eyeToggle1.setOnClickListener {
                if (isPasswordVisible1) {
                    retype.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    eyeToggle1.setImageResource(R.drawable.eyeclosed)
                    isPasswordVisible1 = false
                    retype.setSelection(retype.text.length)
                } else {
                    retype.inputType = InputType.TYPE_CLASS_TEXT
                    eyeToggle1.setImageResource(R.drawable.eyeopen)
                    isPasswordVisible1 = true
                    retype.setSelection(retype.text.length)
                }
            }







        signupBtn.setOnClickListener {
                    val emailStr = email.text.toString()
                    val nameStr = sellerNameEt.text.toString()
                    val phoneStr = sellerPhoneEt.text.toString()
                    val passStr = pass.text.toString()
                    val passRetypeStr = retype.text.toString()

                    if (emailStr.isNotEmpty() && passStr.isNotEmpty() && nameStr.isNotEmpty() && phoneStr.isNotEmpty() && passRetypeStr.isNotEmpty()) {
                        if (passStr == passRetypeStr) {
                            firebaseAuth.createUserWithEmailAndPassword(emailStr, passStr)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val user = firebaseAuth.currentUser
                                        editor.putString(Constants.SELLER_NAME_PATH, nameStr)
                                        editor.putString(Constants.SELLER_PHONE_PATH, phoneStr)
                                        editor.apply()
                                        val userData = hashMapOf(
                                            "name" to nameStr,
                                            "phone" to phoneStr,
                                            "seller" to true,
                                            Constants.POINTS_FIELD to 0,
                                            Constants.BALANCE_FIELD to 0.0
                                        )
                                        user?.uid?.let { userId ->
                                            firestore.collection(Constants.SELLERS_COLLECTION)
                                                .document(userId)
                                                .set(userData)
                                                .addOnSuccessListener {
                                                    val intent = Intent(
                                                        this@SellerSignup,
                                                        LoginActivity::class.java
                                                    )
                                                    startActivity(intent)
                                                }
                                                .addOnFailureListener { e ->
                                                    Toast.makeText(
                                                        this@SellerSignup,
                                                        "Error adding user data to Firestore: ${e.localizedMessage}",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                        }
                                    } else {
                                        Toast.makeText(
                                            this@SellerSignup,
                                            "Error: ${task.exception?.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        } else {
                            Toast.makeText(
                                this@SellerSignup,
                                "Passwords do not match",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@SellerSignup,
                            "Please fill in all fields",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

