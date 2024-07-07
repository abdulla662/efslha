package com.example.efslha

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firestore.v1.StructuredQuery.Order

class UnconfirmedOrder : AppCompatActivity() {
    lateinit var databaseReference: DatabaseReference
    lateinit var databaseReference2: DatabaseReference
    lateinit var databaseReferencecheckpackage: DatabaseReference
    lateinit var databaseReferencechecksep: DatabaseReference
    lateinit var trash: ImageView
    lateinit var trash1: ImageView
    var counterseparate11 = true
    lateinit var confirmorder: Button
    lateinit var result: TextView
    lateinit var result2: TextView
    var check = true
    var check1 = true
    private lateinit var currentUserID: String // Assuming you have a variable to hold the current user's ID
    lateinit var layout: LinearLayout
    private var isSelectedResult = false
    private var isSelectedResult2 = false
    private lateinit var sp: SharedPreferences

    private var canCount = 0;
    private var glassCount = 0;
    private var plasticCount = 0;
    private var paperCount = 0;

    private var canCountSeparate = 0;
    private var glassCountSeparate = 0;
    private var plasticCountSeparate = 0;
    private var paperCountSeparate = 0;
    private fun calculatePoints(
        canCount: Int,
        glassCount: Int,
        plasticCount: Int,
        paperCount: Int
    ): Int {
        return (canCount * Constants.CAN_VALUE) + (glassCount * Constants.GLASS_VALUE) + (plasticCount * Constants.PLASTIC_VALUE) + (paperCount * Constants.PAPER_VALUE)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unconfirmedorder)
        sp = getSharedPreferences(Constants.USER_DATA, MODE_PRIVATE)
        confirmorder = findViewById(R.id.confirmorder)
        result = findViewById(R.id.result11)
        result2 = findViewById(R.id.result2)
        trash1 = findViewById(R.id.trash)
        currentUserID = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        databaseReferencechecksep = FirebaseDatabase.getInstance()
            .getReference("sellers separate/sellers individual/$currentUserID")
        databaseReference = FirebaseDatabase.getInstance().getReference("sellerspackagelater")
        trash = findViewById(R.id.trash)
        databaseReference2 =
            FirebaseDatabase.getInstance().getReference("sellers separate later")
        databaseReferencecheckpackage =
            FirebaseDatabase.getInstance().getReference("sellerspackage/sellers/$currentUserID")
        if (counterseparate11) {
            readdata()
            readdata22()
        } else {
            Toast.makeText(this, "error in retrieving counter", Toast.LENGTH_SHORT).show()
        }

        result2.setOnClickListener {
            returnBackgrounds()
            // Toggle the background color between the original and the new color
            if (result2.background.constantState == ContextCompat.getDrawable(
                    this,
                    R.drawable.plaintextfigure
                )?.constantState
            ) {
                check = false
                result2.setBackgroundResource(R.drawable.new_background)
            } else {
                // Set the original background drawable resource
                result2.setBackgroundResource(R.drawable.plaintextfigure)
            }
            confirmorder.setOnClickListener {
                if (!check) {
                    databaseReferencechecksep.addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists() && snapshot.childrenCount > 0) {
                                // Data exists, show toast message
                                Toast.makeText(
                                    this@UnconfirmedOrder,
                                    "You already have an order",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                // Data doesn't exist or is empty, add new data to Firebase
                                val sellerName = sp.getString(Constants.SELLER_NAME_PATH, null)?:""


                                val quantity =
                                    if (paperCountSeparate > 0) paperCountSeparate
                                    else if (canCountSeparate > 0) canCountSeparate
                                    else if (glassCountSeparate > 0) glassCountSeparate
                                    else plasticCountSeparate
                                val selectedOfferKey =
                                    if (paperCountSeparate > 0) Constants.PAPER_COUNT_KEY
                                    else if (canCountSeparate > 0) Constants.CAN_COUNT_KEY
                                    else if (glassCountSeparate > 0) Constants.GLASS_COUNT_KEY
                                    else Constants.PLASTIC_COUNT_KEY

                                val offerType =
                                    if (paperCountSeparate > 0) Constants.PAPER_OFFER_TYPE
                                    else if (canCountSeparate > 0) Constants.CAN_OFFER_TYPE
                                    else if (plasticCountSeparate > 0) Constants.PLASTIC_OFFER_TYPE
                                    else Constants.GLASS_OFFER_TYPE
                                val data = hashMapOf<String, Any>(
                                    selectedOfferKey to quantity,
                                    Constants.OFFER_TYPE to offerType,
                                    Constants.SELLER_NAME_PATH to sellerName,
                                    Constants.POINTS_CHILD to calculatePoints(
                                        paperCountSeparate,
                                        canCountSeparate,
                                        plasticCountSeparate,
                                        glassCountSeparate
                                    )
                                )


                                databaseReferencechecksep.setValue(data)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            // Data added successfully
                                            Toast.makeText(
                                                this@UnconfirmedOrder,
                                                "Data added successfully",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            removedata2()
                                        } else {
                                            // Failed to add data
                                            Toast.makeText(
                                                this@UnconfirmedOrder,
                                                "Failed to add data",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle database error
                            Toast.makeText(
                                this@UnconfirmedOrder,
                                "Database error: ${error.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                }
            }

        }
        trash.setOnClickListener {
            if (!check) {
                removedata2()
            }
        }

        result.setOnClickListener {
            returnBackgrounds()
            // Toggle the background color between the original and the new color
            if (result.background.constantState == ContextCompat.getDrawable(
                    this,
                    R.drawable.plaintextfigure
                )?.constantState
            ) {
                // Set the new background drawable resource
                result.setBackgroundResource(R.drawable.new_background)
                check1 = false
            } else {
                // Set the original background drawable resource
                result.setBackgroundResource(R.drawable.plaintextfigure)
            }
            confirmorder.setOnClickListener {
                if (!check1) {
                    databaseReferencecheckpackage.addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists() && snapshot.childrenCount > 0) {
                                // Data exists, show toast message
                                Toast.makeText(
                                    this@UnconfirmedOrder,
                                    "You already have an order",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                // Data doesn't exist or is empty, add new data to Firebase
                                val sellerName = sp.getString(Constants.SELLER_NAME_PATH, null)?:""
                                // Parse the text of the result TextView
                                val data = hashMapOf<String, Any>(
                                    Constants.PAPER_COUNT_KEY to paperCount,
                                    Constants.CAN_COUNT_KEY to canCount,
                                    Constants.PLASTIC_COUNT_KEY to plasticCount,
                                    Constants.GLASS_COUNT_KEY to glassCount,
                                    Constants.SELLER_NAME_PATH to sellerName,
                                    Constants.POINTS_CHILD to calculatePoints(
                                        paperCount,
                                        canCount,
                                        plasticCount,
                                        glassCount
                                    )
                                )

                                databaseReferencecheckpackage.setValue(data)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            // Data added successfully
                                            Toast.makeText(
                                                this@UnconfirmedOrder,
                                                "Data added successfully",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            removeData()
                                        } else {
                                            // Failed to add data
                                            Toast.makeText(
                                                this@UnconfirmedOrder,
                                                "Failed to add data",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle database error
                            Toast.makeText(
                                this@UnconfirmedOrder,
                                "Database error: ${error.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })


                }

            }
            trash.setOnClickListener {
                if (!check1) {
                    removeData()
                }
            }
        }
//        result.setOnLongClickListener { v ->
//            val dragShadow = View.DragShadowBuilder(v)
//            v.startDragAndDrop(null, dragShadow, v, 0)
//            true
//        }
//
//        result2.setOnLongClickListener { v ->
//            val dragShadow = View.DragShadowBuilder(v)
//            v.startDragAndDrop(null, dragShadow, v, 0)
//            true
//        }

        // Set drag listener for the trash ImageView
        trash.setOnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DROP -> {
                    val view = event.localState as View
                    val draggedTextView = view as TextView
                    if (draggedTextView === result) {
                        removeData()
                    } else if (draggedTextView === result2) {
                        removedata2()
                    }
                    true
                }

                else -> false
            }
        }
    }

    private fun returnBackgrounds() {
        result.setBackgroundResource(R.drawable.plaintextfigure)
        result2.setBackgroundResource(R.drawable.plaintextfigure)
    }




    fun help(view: View) {
        val a = Intent(this, Help1::class.java)
        startActivity(a)
    }

    fun wallet(view: View) {
        val a = Intent(this, WalletActivity::class.java)
        startActivity(a)
    }


    fun orderthis1(view: View) {
        val a = Intent(this, OrderHistory::class.java)
        startActivity(a)
    }

    private fun readdata() {
        databaseReference = FirebaseDatabase.getInstance()
            .getReference("sellerspackagelater/sellers later/$currentUserID")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    paperCount =
                        snapshot.child(Constants.PAPER_COUNT_KEY).getValue(Int::class.java) ?: 0
                    canCount =
                        snapshot.child(Constants.CAN_COUNT_KEY).getValue(Int::class.java) ?: 0
                    glassCount =
                        snapshot.child(Constants.GLASS_COUNT_KEY).getValue(Int::class.java) ?: 0
                    plasticCount =
                        snapshot.child(Constants.PLASTIC_COUNT_KEY).getValue(Int::class.java) ?: 0


                    val text =
                        "paper: $paperCount, can: $canCount, glass: $glassCount, plastic: $plasticCount"
                    result.text = "$text"
                } else {
                    result.text = " "
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@UnconfirmedOrder,
                    "Error fetching data: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }


    private fun readdata22() {
        databaseReference2 = FirebaseDatabase.getInstance()
            .getReference("sellers separate later/sellers individual later/$currentUserID")
        databaseReference2.get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                paperCountSeparate = dataSnapshot.child(Constants.PAPER_COUNT_KEY).value?.toString()?.toInt() ?: 0
               canCountSeparate= dataSnapshot.child(Constants.CAN_COUNT_KEY).value?.toString()?.toInt() ?: 0
               glassCountSeparate = dataSnapshot.child(Constants.GLASS_COUNT_KEY).value?.toString()?.toInt() ?: 0
              plasticCountSeparate= dataSnapshot.child(Constants.PLASTIC_COUNT_KEY).value?.toString()?.toInt() ?: 0



                var text = ""
                if (paperCountSeparate > 0) {
                    text += "paper: $paperCountSeparate\n"
                }
                if (canCountSeparate > 0) {
                    text += "can: $canCountSeparate\n"
                }
                if (glassCountSeparate > 0) {
                    text += "glass: $glassCountSeparate\n"
                }
                if (plasticCountSeparate > 0) {
                    text += "plastic: $plasticCountSeparate\n"
                }

                // Set the text of result2 to display the category names and their quantities
                result2.text = text.trim()

                // Now you can use totalQuantityInt for further processing or upload it to Firebase
            } else {
                result2.text = " "
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error fetching data:", Toast.LENGTH_LONG).show()
        }
    }


    private fun removeData() {
        // Step 1: Get a reference to the database
        val database = FirebaseDatabase.getInstance()
        val reference = database.reference.child("sellerspackagelater/sellers later/$currentUserID")

        // Step 3: Call removeValue() method to delete the data
        reference.removeValue()
    }

    fun removedata2() {
        val database = FirebaseDatabase.getInstance()
        val reference = database.reference.child(
            "sellers separate later/sellers individual later/$currentUserID"
        )

        // Step 3: Call removeValue() method to delete the data
        reference.removeValue()

    }

    private fun deleteData(textView: TextView) {
        textView.text = ""
    }
}