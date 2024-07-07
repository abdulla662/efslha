package com.example.efslha

import android.content.Intent
import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.efslha.databinding.ActivityWalletBinding

import com.example.efslha.ui.main.Feedback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firestore.v1.StructuredQuery.Order
import java.util.Locale
import kotlin.time.times

class WalletActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWalletBinding
    private lateinit var db: FirebaseFirestore
    private var id: String? = null
    @RequiresApi(Build.VERSION_CODES.N)
    private val numberFormat: NumberFormat = NumberFormat.getInstance(Locale(Constants.ENGLISH_RES))


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletBinding.inflate(layoutInflater)
        numberFormat.minimumFractionDigits = 1
        numberFormat.maximumFractionDigits = 2
        db = FirebaseFirestore.getInstance()
        id = FirebaseAuth.getInstance().uid
        if (id == null) {
            Toast.makeText(this@WalletActivity, "Error while loading data", Toast.LENGTH_SHORT)
                .show()
            finish()
            return
        }

        binding.apply {
            setContentView(root)

            db.collection(Constants.SELLERS_COLLECTION)
                .document(id!!).get()
                .addOnCompleteListener {

                    if (it.isSuccessful && it.result != null) {
                        val documentSnap = it.result
                        val points = documentSnap.getLong(Constants.POINTS_FIELD)
                        val balance = documentSnap.getDouble(Constants.BALANCE_FIELD)
                        pointsTv.text = "$points"

                    } else {
                        Toast.makeText(
                            this@WalletActivity,
                            "Error: ${it.exception?.localizedMessage ?: "Unknown error occurred"}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            binding.redeemBalanceBtn.setOnClickListener {

                val a = Intent(this@WalletActivity, choosefromwallet::class.java)
                startActivity(a)


            }

        }
    }

    fun help(view: View) {
        val a = Intent(this,Help1::class.java)
        startActivity(a)
    }
    fun history(view: View) {
        val a = Intent(this,OrderHistory::class.java)
        startActivity(a)
    }
    fun wallet(view: View) {
        val a = Intent(this,WalletActivity::class.java)
        startActivity(a)
    }

    fun feedback(view: View) {
        val a=Intent(this,Feedback::class.java)
        startActivity(a)

    }
}
