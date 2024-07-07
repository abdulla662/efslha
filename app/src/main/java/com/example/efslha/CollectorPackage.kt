package com.example.efslha

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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

import com.example.efslha.databinding.ActivityCollectorpackageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore


class CollectorPackage : AppCompatActivity() {
    private lateinit var binding: ActivityCollectorpackageBinding
    private lateinit var ref: DatabaseReference
    private lateinit var db: FirebaseFirestore
    var check = true
    var userName: String = ""
    var username1: String = ""
    var userId1: String = ""
    val currentUserId1 = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var quantity1: Int = 0
    private lateinit var offerDatabaseRef: DatabaseReference
    private lateinit var databaseReference: DatabaseReference
    var canCount: Int = 0
    var glassCount: Int = 0
    var paperCount: Int = 0
    var plasticCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollectorpackageBinding.inflate(layoutInflater)
        db = FirebaseFirestore.getInstance()
        ref = FirebaseDatabase.getInstance().getReference("sellerspackage/sellers")
        offerDatabaseRef = FirebaseDatabase.getInstance().getReference(Constants.OFFERS_PACKAGE_CHILD)
        databaseReference = FirebaseDatabase.getInstance().reference

        binding.apply {
            setContentView(root)
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userMaterialsMap = mutableMapOf<String, MutableList<String>>()

                    for (sellerSnapshot in dataSnapshot.children) {
                        val userId = sellerSnapshot.key ?: continue
                        val materialsList = userMaterialsMap.getOrPut(userId) { mutableListOf() }

                        sellerSnapshot.children.forEach { item ->
                            if (item.key != Constants.POINTS_CHILD) {
                                val quantity =
                                    (item.value as? Long ?: (item.value as? String)?.toLongOrNull()
                                    ?: 0L).toInt()
                                val material = item.key ?: "unknown"
                                materialsList.add("$material,$quantity")
                            }
                        }
                    }
                    userMaterialsMap.forEach { (userId, materialsList) ->
                        val displayText = materialsList.joinToString("\n")
                        val newTextView = createNewTextView(displayText, userId)
                        packagesScrollView.addView(newTextView)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@CollectorPackage,
                        "Database error: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
            collectBtn.setOnClickListener {
                if (userId1 == "") {
                    Toast.makeText(
                        this@CollectorPackage,
                        "there is no id selected ",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    offerDatabaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            var offerExists = false
                            for (offerSnapshot in dataSnapshot.children) {
                                val collectorId =
                                    offerSnapshot.child(Constants.COLLECTOR_ID_PATH).getValue(String::class.java)
                                val userId =
                                    offerSnapshot.child(Constants.USER_ID_PATH).getValue(String::class.java)

                                if (collectorId == currentUserId1 && userId == userId1) {
                                    offerExists = true
                                    Toast.makeText(
                                        this@CollectorPackage,
                                        "you offered this seller before",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    break
                                }
                            }
                            if (!offerExists) {
                                getUserDetails()

                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })

                }
            }

        }
    }

    private fun createNewTextView(quantity: String, userId: String?): TextView {
        val newTextView = TextView(this)
        newTextView.setBackgroundResource(R.drawable.plaintextfigure)
        newTextView.typeface = ResourcesCompat.getFont(this, R.font.inter_semibold)
        newTextView.gravity = Gravity.CENTER
        newTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        newTextView.setOnClickListener {
            if (newTextView.background.constantState == ContextCompat.getDrawable(
                    this, R.drawable.plaintextfigure
                )?.constantState
            ) {

                newTextView.setBackgroundResource(R.drawable.new_background)
                userId1 = userId.toString()
            } else {
                newTextView.setBackgroundResource(R.drawable.plaintextfigure)

            }
        }

        userId.toString()?.let { id ->
            db.collection(Constants.SELLERS_COLLECTION).document(id).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        userName = document.getString("name").toString()
                        newTextView.text = "$userName offer to sell amount of:$quantity"
                    } else {
                        newTextView.text = " Quantity is : $quantity, User Name: Not Found"
                    }

                }
                .addOnFailureListener {
                    newTextView.text =
                        " Quantity is : $quantity, User ID: $userId, User Name: Error Retrieving"
                }
        }

        val widthInPixels = dpToPx(400)
        val heightInPixels = dpToPx(120)

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


    fun help(view: View) {
        val a = Intent(this, HelpActivity::class.java)
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

    private fun dpToPx(dp: Int): Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        resources.displayMetrics
    )

    fun getUserDetails() {
        databaseReference.child("sellerspackage/sellers/$userId1")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        canCount = (dataSnapshot.child("can").value as Long? ?: 0).toInt()
                        glassCount = (dataSnapshot.child("glass").value as Long? ?: 0).toInt()
                        paperCount = (dataSnapshot.child("paper").value as Long? ?: 0).toInt()
                        plasticCount = (dataSnapshot.child("plastic").value as Long? ?: 0).toInt()
                        val offerDetails = hashMapOf(
                            "can" to canCount,
                            "glass" to glassCount,
                            "paper" to paperCount,
                            "plastic" to plasticCount,
                            Constants.USER_ID_PATH to userId1,
                            Constants.COLLECTOR_ID_PATH to currentUserId1
                        )
                        databaseReference.child(Constants.OFFERS_PACKAGE_CHILD).push().setValue(offerDetails)
                            .addOnSuccessListener {
                                val intent = Intent(this@CollectorPackage, CollectionCodeActivity::class.java)
                                intent.putExtra(Constants.USER_ID_KEY,userId1)
                                intent.putExtra(Constants.PLASTIC_COUNT_KEY,plasticCount)
                                intent.putExtra(Constants.CAN_COUNT_KEY,canCount)
                                intent.putExtra(Constants.GLASS_COUNT_KEY,glassCount)
                                intent.putExtra(Constants.PAPER_COUNT_KEY,paperCount)
                                startActivity(intent)
                                Toast.makeText(this@CollectorPackage, "Uploaded Successfully", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                // Handle failure
                                Toast.makeText(
                                    this@CollectorPackage,
                                    " uploaded failed: ${it.localizedMessage}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}








