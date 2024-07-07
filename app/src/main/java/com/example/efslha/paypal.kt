package com.example.efslha

import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.efslha.databinding.ActivityPaypalBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class paypal : AppCompatActivity() {
    private lateinit var binding: ActivityPaypalBinding
    private lateinit var paypallink: EditText
    private lateinit var money: TextView
    private lateinit var confirm: Button
    private lateinit var db: FirebaseFirestore
    private var id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaypalBinding.inflate(layoutInflater)
        db = FirebaseFirestore.getInstance()
        id = FirebaseAuth.getInstance().uid
        setContentView(binding.root)

        paypallink = binding.paypallink // Assuming the ID of EditText for PayPal link is paypallink
        money = binding.money // Assuming the ID of TextView for money amount is money
        confirm = binding.confirm // Assuming the ID of Button for confirmation is confirm

        db.collection(Constants.SELLERS_COLLECTION)
            .document(id!!).get()
            .addOnCompleteListener {
                if (it.isSuccessful && it.result != null) {
                    val documentSnap = it.result
                    val points = documentSnap.getLong(Constants.POINTS_FIELD) ?: 0
                    val balance = documentSnap.getDouble(Constants.BALANCE_FIELD) ?: 0.0
                    val lastPoints = documentSnap.getLong("lastPoints") ?: 0

                    if (points != lastPoints) {
                        val updatedMap = hashMapOf<String, Any>(
                            Constants.BALANCE_FIELD to (points * Constants.BALANCE_COEFFICIENT),
                            "lastPoints" to points
                        )
                        db.collection(Constants.SELLERS_COLLECTION)
                            .document(id!!).update(updatedMap)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    money.text = "${(points * Constants.BALANCE_COEFFICIENT)} EGP"
                                } else {
                                    Toast.makeText(
                                        this@paypal, "Error: ${it.exception?.localizedMessage ?: ""}", Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        money.text = "$balance EGP"
                    }

                } else {
                    Toast.makeText(
                        this@paypal,
                        "Error: ${it.exception?.localizedMessage ?: "Unknown error occurred"}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        confirm.setOnClickListener {
            val paypalLinkText = paypallink.text.toString().trim()
            val moneyAmount = money.text.toString().replace(" EGP", "").trim().toDoubleOrNull()

            if (!paypalLinkText.startsWith("PayPal.Me/", ignoreCase = true)) {
                Toast.makeText(this@paypal, "Please enter a valid PayPal link starting with PayPal.Me/", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (moneyAmount == null || moneyAmount < 100) {
                Toast.makeText(this@paypal, "You should have a minimum amount of 100 EGP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val deductionAmount = moneyAmount * 10 / 100
            val dialogBuilder = AlertDialog.Builder(this@paypal)
            dialogBuilder.setMessage("We will deduct 10% (${deductionAmount} EGP) from your money. Do you accept?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    performTransaction()
                }
                .setNegativeButton("No") { dialog, id ->
                    dialog.dismiss()
                }

            val alert = dialogBuilder.create()
            alert.setTitle("Confirm Transaction")
            alert.show()
        }
    }

    private fun performTransaction() {
        val updatedMap = hashMapOf<String, Any>(
            Constants.BALANCE_FIELD to 0.0,
            Constants.POINTS_FIELD to 0,
            "lastPoints" to 0
        )

        db.collection(Constants.SELLERS_COLLECTION)
            .document(id!!).update(updatedMap)
            .addOnCompleteListener { updateTask ->
                if (updateTask.isSuccessful) {
                    Toast.makeText(
                        this@paypal,
                        "Transaction completed successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    money.text = "0 EGP"
                } else {
                    Toast.makeText(
                        this@paypal,
                        "Error: ${updateTask.exception?.localizedMessage ?: ""}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}
