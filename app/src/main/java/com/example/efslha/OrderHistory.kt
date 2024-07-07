package com.example.efslha

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

import com.example.efslha.databinding.ActivityOrderhistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class OrderHistory : AppCompatActivity() {
    private lateinit var DatabaseReference112: DatabaseReference
    var counterdata = 0
    var counterseparate11 = true
    var check = true
    var check1 = true

    lateinit var currentUserID: String
    private lateinit var binding: ActivityOrderhistoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        currentUserID = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (currentUserID.isEmpty()) finish()
        readdata()
        readdata22()
        super.onCreate(savedInstanceState)
        binding = ActivityOrderhistoryBinding.inflate(layoutInflater)
        binding.apply {

            setContentView(root)
            orderHistoryBtn.setOnClickListener {
                val a = Intent(this@OrderHistory, userpage::class.java)
                startActivity(a)
            }
            cancelOrderBtn.setOnClickListener {
                if (!check) {
                    removeData()
                }
                if (!check1) {
                    removeData2()
                }
            }

            resultTv1.setOnClickListener {
                if (resultTv1.background.constantState == ContextCompat.getDrawable(
                        this@OrderHistory,
                        R.drawable.plaintextfigure
                    )?.constantState
                ) {
                    check = false
                    resultTv1.setBackgroundResource(R.drawable.new_background)
                    resultTv1.setTextColor(Color.WHITE)
                } else {
                    check = true
                    resultTv1.setBackgroundResource(R.drawable.plaintextfigure)
                    resultTv1.setTextColor(Color.BLACK)
                }
            }

            resultTv2.setOnClickListener {
                // Toggle the background color between the original and the new color
                if (resultTv2.background.constantState == ContextCompat.getDrawable(
                        this@OrderHistory,
                        R.drawable.plaintextfigure
                    )?.constantState
                ) {
                    check1 = false
                    resultTv2.setBackgroundResource(R.drawable.new_background)
                    resultTv2.setTextColor(Color.WHITE)
                } else {
                    check1 = true
                    resultTv2.setBackgroundResource(R.drawable.plaintextfigure)
                    resultTv2.setTextColor(Color.BLACK)
                }
            }
        }

    }

    fun wallet(view: View) {
        val a = Intent(this@OrderHistory, WalletActivity::class.java)
        startActivity(a)
    }

//    fun history(view: View) {
//
//    }

    fun help(view: View) {
        val a = Intent(this, Help1::class.java)
        startActivity(a)
    }


    private fun readdata() {
        DatabaseReference112 =
            FirebaseDatabase.getInstance().getReference("sellerspackage/sellers/$currentUserID")
        DatabaseReference112.get().addOnSuccessListener {
            if (it.exists()) {
                val paper = it.child(Constants.PAPER_COUNT_KEY).value?.toString()?.toInt()
                val can = it.child(Constants.CAN_COUNT_KEY).value?.toString()?.toInt()
                val glass = it.child(Constants.GLASS_COUNT_KEY).value?.toString()?.toInt()
                val plastic = it.child(Constants.GLASS_COUNT_KEY).value?.toString()?.toInt()
                val text = "paper: $paper, can: $can, glass: $glass, Plastic: $plastic"
                binding.resultTv1.setText("you request to sell $text").toString()
            } else {
                binding.resultTv1.setText(" ")
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error fetching data:", Toast.LENGTH_LONG).show()
        }

    }


    private fun readdata22() {
        DatabaseReference112 = FirebaseDatabase.getInstance()
            .getReference("sellers separate/sellers individual/$currentUserID")
        DatabaseReference112.get().addOnSuccessListener {
            if (it.exists()) {
                val paper = it.child(Constants.PAPER_COUNT_KEY).value?.toString()?.toInt()
                val can = it.child(Constants.CAN_COUNT_KEY).value?.toString()?.toInt()
                val glass = it.child(Constants.GLASS_COUNT_KEY).value?.toString()?.toInt()
                val plastic = it.child(Constants.PLASTIC_COUNT_KEY).value?.toString()?.toInt()

                var text = ""
                if (paper != null && paper > 0) {
                    text += "request to sell amount of paper: $paper\n"
                }
                if (can != null && can > 0) {
                    text += "request to sell amount of can: $can\n"
                }
                if (glass != null && glass > 0) {
                    text += "request to sell amount of glass: $glass\n"
                }
                if (plastic != null && plastic > 0) {
                    text += "request to sell amount of Plastic: $plastic\n"
                }
                binding.resultTv2.text = text
            } else {
                binding.resultTv2.setText(" ")
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error fetching data:", Toast.LENGTH_LONG).show()
        }
    }

    private fun removeData() {
        val database = FirebaseDatabase.getInstance()
        val reference = database.reference.child("sellerspackage/sellers/$currentUserID")

        // Step 3: Call removeValue() method to delete the data
        reference.removeValue()
    }

    private fun removeData2() {
        val database = FirebaseDatabase.getInstance()
        val reference =
            database.reference.child("sellers separate/sellers individual/$currentUserID")

        // Step 3: Call removeValue() method to delete the data
        reference.removeValue()
    }

}


