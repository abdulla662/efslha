package com.example.efslha

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.efslha.ui.main.Feedback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class PaperActivity : AppCompatActivity() {
    private lateinit var ref: DatabaseReference
    lateinit var scrollViewLayout: LinearLayout
    private lateinit var context: Context
    private lateinit var db: FirebaseFirestore
    lateinit var upload1: Button
    var check = true
    var userName: String = ""
    var username1: String = ""
    var userId1: String = ""
    val currentUserId1 = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var quantity1:Int=0
    private lateinit var offerDatabaseRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paper)
        db = FirebaseFirestore.getInstance()
        upload1 = findViewById(R.id.upload1)
        ref = FirebaseDatabase.getInstance().getReference("sellers separate/sellers individual")
        scrollViewLayout = findViewById(R.id.scrollViewLayout)
        offerDatabaseRef = FirebaseDatabase.getInstance().getReference(Constants.OFFERS_PAPER_CHILD)
        context = this
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (sellerSnapshot in dataSnapshot.children) {
                    if (sellerSnapshot.hasChild("paper")) {
                        val quantity = sellerSnapshot.child("paper").getValue(Integer::class.java)
                        val userId = sellerSnapshot.key

                        val newTextView = createNewTextView(quantity.toString(), userId)
                        scrollViewLayout.addView(newTextView)

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        upload1.setOnClickListener {
            if (userId1 == "") {
                Toast.makeText(this, "there is no id selected ", Toast.LENGTH_LONG).show()
            } else {
                val offer = hashMapOf(
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
                                            val intent = Intent(this@PaperActivity, CollectionCodeActivity::class.java)
                                            intent.putExtra(Constants.USER_ID_KEY,userId1)
                                            intent.putExtra(Constants.PAPER_COUNT_KEY,quantity1)
                                            startActivity(intent)
                                            Toast.makeText(this@PaperActivity, "Uploaded Successfully", Toast.LENGTH_SHORT).show()

                                        } else {
                                            Toast.makeText(
                                                this@PaperActivity,
                                                "Error please try again later",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(this@PaperActivity, "Error", Toast.LENGTH_LONG)
                                    .show()
                            }
                        } else {
                            Toast.makeText(
                                this@PaperActivity,
                                "You have already made an offer to this seller",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(
                            this@PaperActivity,
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
        val a = Intent(this,CollectorCollections::class.java)
        startActivity(a)
    }
    fun msgcenter(view: View) {
        val a = Intent(this,Feedback::class.java)
        startActivity(a)
    }
    fun help(view: View){
        val a = Intent(this,HelpActivity::class.java)
        startActivity(a)
    }
    private fun createNewTextView(quantity: String, userId: String?): TextView {

        val newTextView = TextView(this)
        newTextView.setBackgroundResource(R.drawable.plaintextfigure)
        newTextView.typeface = ResourcesCompat.getFont(this, R.font.inter_semibold)
        newTextView.gravity = Gravity.CENTER
        newTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        newTextView.text = "paper Quantity is : $quantity, User ID: $userId"
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
                        newTextView.text = "$userName offer to sell amount of paper :$quantity"
                    } else {
                        newTextView.text = "paper Quantity is : $quantity, User Name: Not Found"
                    }

                }
                .addOnFailureListener {
                    newTextView.text =
                        "paper Quantity is : $quantity, User ID: $userId, User Name: Error Retrieving"
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

        return newTextView // Return the newly created TextView
    }
}