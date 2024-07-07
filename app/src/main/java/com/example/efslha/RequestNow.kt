package com.example.efslha

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class RequestNow : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    lateinit var codeTextView: TextView
    lateinit var change: Button
    var status=true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_requestnow)

        val userId = intent.getStringExtra("userid")
        firestore = FirebaseFirestore.getInstance()

        codeTextView = findViewById(R.id.code)
        change = findViewById(R.id.change)

        // Fetch code from Firestore when the activity is created
        fetchCodeFromFirestore(userId)
        change.setOnClickListener {
            changeCode()
        }
    }

    private fun fetchCodeFromFirestore(userId: String?) {
        if (userId != null) {
            val docRef = firestore.collection(Constants.SELLERS_COLLECTION).document(userId)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val generatedCode = document.getString(Constants.GENERATED_CODE_FIELD)
                        // Update codeTextView with the generated code
                        codeTextView.text = generatedCode
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to fetch code: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun changeCode() {
        val userId = intent.getStringExtra("userid")
        if (userId != null) {
            // Generate a new code
            val generatedCode = generateRandomCode(5)
            // Update codeTextView with the new code
            codeTextView.text = generatedCode
            // Upload the new code to Firebase Firestore
            updateCodeInFirestore(userId, generatedCode)
        }
    }

    private fun generateRandomCode(length: Int): String {
        val allowedChars = ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private fun updateCodeInFirestore(userId: String, generatedCode: String) {
        val docRef = firestore.collection(Constants.SELLERS_COLLECTION).document(userId)
        val data = hashMapOf(
            Constants.GENERATED_CODE_FIELD to generatedCode
        )

        docRef.set(data, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "Code updated in Firestore", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update code in Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun requesters(view: View) {
        val a = Intent(this, Requesters::class.java)
        startActivity(a)
    }

    fun help(view: View) {
        val a = Intent(this, Help1::class.java)
        startActivity(a)
    }

    fun history(view: View) {
        val a = Intent(this, OrderHistory::class.java)
        startActivity(a)
    }

    fun wallet(view: View) {
        val a = Intent(this, WalletActivity::class.java)
        startActivity(a)
    }
}
