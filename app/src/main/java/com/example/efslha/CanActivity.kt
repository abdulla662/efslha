package com.example.efslha

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.efslha.databinding.ActivityCanBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class CanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCanBinding
    private lateinit var ref: DatabaseReference
    lateinit var scrollViewLayout3: LinearLayout
    private lateinit var db: FirebaseFirestore


    var check = true
    var userName: String = ""
    var username1: String = ""
    var userId1: String = ""
    val currentUserId1 = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var quantity1:Int=0
    private lateinit var offerDatabaseRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        ref = FirebaseDatabase.getInstance().getReference("sellers separate/sellers individual")
        scrollViewLayout3 = findViewById(R.id.scrollViewLayout3)
        offerDatabaseRef = FirebaseDatabase.getInstance().getReference(Constants.OFFERS_CAN_CHILD)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (sellerSnapshot in dataSnapshot.children) {
                    if (sellerSnapshot.hasChild("can")) {
                        val quantity = sellerSnapshot.child("can").getValue(Integer::class.java)
                        val userId = sellerSnapshot.key

                        val newTextView = createNewTextView(quantity.toString(), userId)
                        scrollViewLayout3.addView(newTextView)

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        binding.uploadCan.setOnClickListener {
            if (userId1 == "") {
                Toast.makeText(this, "there is no id selected ", Toast.LENGTH_LONG).show()
            } else {
                val offer = hashMapOf<String,Any>(
                    Constants.USER_ID_PATH to userId1,
                    Constants.COLLECTOR_ID_PATH to currentUserId1,
                    Constants.QUANTITY_PATH to quantity1
                )

                offerDatabaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var offerExists = false

                        for (offerSnapshot in dataSnapshot.children) {
                            val collectorId =
                                offerSnapshot.child(Constants.COLLECTOR_ID_PATH).getValue(String::class.java)
                            val userId = offerSnapshot.child(Constants.USER_ID_PATH).getValue(String::class.java)

                            if (collectorId == currentUserId1 && userId == userId1) {
                                offerExists = true
                                break
                            }
                        }

                        if (!offerExists) {
                            val offerId = offerDatabaseRef.push().key
                            if (offerId != null) {
                                offerDatabaseRef.child(offerId).setValue(offer)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val intent = Intent(this@CanActivity, CollectionCodeActivity::class.java)
                                            intent.putExtra(Constants.USER_ID_KEY,userId1)
                                            intent.putExtra(Constants.CAN_COUNT_KEY,quantity1)
                                            startActivity(intent)
                                            Toast.makeText(this@CanActivity, "Uploaded Successfully", Toast.LENGTH_SHORT).show()

                                        } else {
                                            Toast.makeText(
                                                this@CanActivity,
                                                "Error please try again later",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(this@CanActivity, "Error", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(
                                this@CanActivity,
                                "You have already made an offer to this seller",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(
                            this@CanActivity,
                            "Database error: ${databaseError.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
            }


        }
    }
    private fun dpToPx(dp: Int): Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        resources.displayMetrics
    )

    fun collections(view: View) {
        val a = Intent(this, CollectorCollections::class.java)
        startActivity(a)
    }
    fun msgcenter(view: View) {
        val a = Intent(this, MessageCenterActivity::class.java)
        startActivity(a)
    }
    fun help(view: View){
        val a = Intent(this, HelpActivity::class.java)
        startActivity(a)
    }
    private fun createNewTextView(quantity: String, userId: String?): TextView {

        val newTextView = TextView(this)
        newTextView.setBackgroundResource(R.drawable.plaintextfigure)
        newTextView.typeface = ResourcesCompat.getFont(this, R.font.inter_semibold)
        newTextView.gravity = Gravity.CENTER
        newTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        newTextView.text = "Can Quantity is : $quantity, User ID: $userId"
        newTextView.setOnClickListener {
            if (newTextView.background.constantState == ContextCompat.getDrawable(
                    this, R.drawable.plaintextfigure
                )?.constantState
            ) {
                newTextView.setBackgroundResource(R.drawable.new_background)
                userId1 = userId.toString()
                quantity1=quantity.toInt()

            } else {
                newTextView.setBackgroundResource(R.drawable.plaintextfigure)
            }
        }

        userId.toString()?.let { id ->
            db.collection(Constants.SELLERS_COLLECTION).document(id).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        userName = document.getString("name").toString()
                        newTextView.text = "$userName offer to sell amount of Can :$quantity"
                    } else {
                        newTextView.text = "Can Quantity is : $quantity, User Name: Not Found"
                    }

                }
                .addOnFailureListener {
                    newTextView.text =
                        "Can Quantity is : $quantity, User ID: $userId, User Name: Error Retrieving"
                }
        }

        val widthInPixels = dpToPx(360)
        val heightInPixels = dpToPx(62)

        val layoutParams = LinearLayout.LayoutParams(widthInPixels.toInt(), heightInPixels.toInt())
        layoutParams.gravity = Gravity.CENTER

        val marginInPixels = dpToPx(24)
        layoutParams.setMargins(
            marginInPixels.toInt(),
            marginInPixels.toInt(),
            marginInPixels.toInt(),
            marginInPixels.toInt()
        )

        newTextView.layoutParams = layoutParams

        return newTextView
    }
}