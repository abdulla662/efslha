package com.example.efslha

import android.content.ContentValues.TAG
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
import com.example.efslha.ui.main.Feedback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class CollectorCollections : AppCompatActivity() {
    lateinit var clear: Button
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    lateinit var userId1: String
    var userName: String = ""
    private lateinit var db: FirebaseFirestore
    var collectorid: String = ""
    var quantity: Int = 0
    var path: String = ""
    var pathInDatabase: String = ""
    var userIdglass: String = ""
    var userIdplastic: String = ""
    var userIdcan: String = ""
    var userIdpackage: String = ""
    var userIdpaper: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collectorcollections)
        clear = findViewById(R.id.clear)
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        db = FirebaseFirestore.getInstance()
        checkUserGlass()
        checkUserPlastic()
        checkUserCan()
        checkUserPackage()
        checkUserPaper()

        clear.setOnClickListener {
            if (pathInDatabase == Constants.OFFERS_GLASS_CHILD) {
                val offersRef = FirebaseDatabase.getInstance().reference.child(Constants.OFFERS_GLASS_CHILD)
                offersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (offerSnapshot in snapshot.children) {
                            val userIdGlass =
                                offerSnapshot.child(Constants.USER_ID_PATH).getValue(String::class.java)
                            val collectorId =
                                offerSnapshot.child(Constants.COLLECTOR_ID_PATH).getValue(String::class.java)

                            if (userIdGlass == userIdglass && collectorId == userId1) {
                                offerSnapshot.ref.removeValue().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            this@CollectorCollections,
                                            "removed succefully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Log.d("DATABASE", "Offer removed successfully.")
                                    } else {
                                        Log.e("DATABASE", "Failed to remove offer.", task.exception)
                                    }
                                }
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handling the error
                        Log.e(
                            "DATABASE",
                            "Could not read from database",
                            databaseError.toException()
                        )
                    }
                })
            }
            if (pathInDatabase == Constants.OFFERS_PLASTIC_CHILD) {

                val offersRef = FirebaseDatabase.getInstance().reference.child(Constants.OFFERS_PLASTIC_CHILD)
                offersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (offerSnapshot in snapshot.children) {
                            val userIdplastic1 =
                                offerSnapshot.child(Constants.USER_ID_PATH).getValue(String::class.java)
                            val collectorId =
                                offerSnapshot.child(Constants.COLLECTOR_ID_PATH).getValue(String::class.java)

                            if (userIdplastic == userIdplastic1 && collectorId == userId1) {
                                offerSnapshot.ref.removeValue().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            this@CollectorCollections,
                                            "removed succefully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Log.d("DATABASE", "Offer removed successfully.")
                                    } else {
                                        Log.e("DATABASE", "Failed to remove offer.", task.exception)
                                    }
                                }
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handling the error
                        Log.e(
                            "DATABASE",
                            "Could not read from database",
                            databaseError.toException()
                        )
                    }
                })
            } else {
                Toast.makeText(this, "there is no orders with that ID", Toast.LENGTH_LONG).show()


            }
            if (pathInDatabase == Constants.OFFERS_CAN_CHILD) {
                val offersRef = FirebaseDatabase.getInstance().reference.child(Constants.OFFERS_CAN_CHILD)
                offersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (offerSnapshot in snapshot.children) {
                            val userIdcan1 =
                                offerSnapshot.child(Constants.USER_ID_PATH).getValue(String::class.java)
                            val collectorId =
                                offerSnapshot.child(Constants.COLLECTOR_ID_PATH).getValue(String::class.java)

                            if (userIdcan1 == userIdcan && collectorId == userId1) {
                                offerSnapshot.ref.removeValue().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            this@CollectorCollections,
                                            "removed succefully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Log.d("DATABASE", "Offer removed successfully.")
                                    } else {
                                        Log.e("DATABASE", "Failed to remove offer.", task.exception)
                                    }
                                }
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handling the error
                        Log.e(
                            "DATABASE",
                            "Could not read from database",
                            databaseError.toException()
                        )
                    }
                })
            }
            if (pathInDatabase == Constants.OFFERS_PACKAGE_CHILD) {
                val offersRef = FirebaseDatabase.getInstance().reference.child(Constants.OFFERS_PACKAGE_CHILD)
                offersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (offerSnapshot in snapshot.children) {
                            val useridpackage1 =
                                offerSnapshot.child(Constants.USER_ID_PATH).getValue(String::class.java)
                            val collectorId =
                                offerSnapshot.child(Constants.COLLECTOR_ID_PATH).getValue(String::class.java)

                            if (useridpackage1 == userIdpackage && collectorId == userId1) {
                                offerSnapshot.ref.removeValue().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            this@CollectorCollections,
                                            "removed successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Log.d("DATABASE", "Offer removed successfully.")
                                    } else {
                                        Log.e("DATABASE", "Failed to remove offer.", task.exception)
                                    }
                                }
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handling the error
                        Log.e(
                            "DATABASE",
                            "Could not read from database",
                            databaseError.toException()
                        )
                    }
                })
            }
            if (pathInDatabase == Constants.OFFERS_PAPER_CHILD) {
                val offersRef = FirebaseDatabase.getInstance().reference.child(Constants.OFFERS_PAPER_CHILD)
                offersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (offerSnapshot in snapshot.children) {
                            val useridpackage1 =
                                offerSnapshot.child(Constants.USER_ID_PATH).getValue(String::class.java)
                            val collectorId =
                                offerSnapshot.child(Constants.COLLECTOR_ID_PATH).getValue(String::class.java)

                            if (useridpackage1 == userIdpaper && collectorId == userId1) {
                                offerSnapshot.ref.removeValue().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            this@CollectorCollections,
                                            "removed succefully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Log.d("DATABASE", "Offer removed successfully.")
                                    } else {
                                        Log.e("DATABASE", "Failed to remove offer.", task.exception)
                                    }
                                }
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handling the error
                        Log.e(
                            "DATABASE",
                            "Could not read from database",
                            databaseError.toException()
                        )
                    }
                })
            }

        }


    }

    private fun createCollectorTextView(data: String, dbPath: String) {
        val newTextView = TextView(this)
        newTextView.text = data
        newTextView.tag = dbPath
        newTextView.setBackgroundResource(R.drawable.plaintextfigure)
        newTextView.typeface = ResourcesCompat.getFont(this, R.font.inter_semibold)
        newTextView.gravity = Gravity.CENTER
        newTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        newTextView.setOnClickListener {
            pathInDatabase = it.tag as String
            if (newTextView.background.constantState == ContextCompat.getDrawable(
                    this, R.drawable.plaintextfigure
                )?.constantState

            ) {
                newTextView.setBackgroundResource(R.drawable.new_background)

            } else {
                newTextView.setBackgroundResource(R.drawable.plaintextfigure)

            }
        }


        val widthInPixels = dpToPx(370)
        val heightInPixels = dpToPx(130)

        val layoutParams = LinearLayout.LayoutParams(widthInPixels, heightInPixels)
        layoutParams.gravity = Gravity.CENTER
        val marginInPixels = dpToPx(24)
        layoutParams.setMargins(marginInPixels, marginInPixels, marginInPixels, marginInPixels)

        newTextView.layoutParams = layoutParams

        val scrollViewLayout = findViewById<LinearLayout>(R.id.scrollViewLayout)
        scrollViewLayout.addView(newTextView)
    }

    private fun dpToPx(dp: Int): Int {
        val scale = resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    fun collections(view: View) {
        val a = Intent(this, CollectorCollections::class.java)
        startActivity(a)
    }

    fun msgcenter(view: View) {
        val a = Intent(this, Feedback::class.java)
        startActivity(a)
    }

    fun help(view: View) {
        val a = Intent(this, HelpActivity::class.java)
        startActivity(a)
    }


    private fun checkUserGlass() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            userId1 = currentUser.uid
            val offersRef = FirebaseDatabase.getInstance().reference.child(Constants.OFFERS_GLASS_CHILD)
            offersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (offerSnapshot in dataSnapshot.children) {
                        val quantity = offerSnapshot.child(Constants.GLASS_COUNT_KEY).getValue(Int::class.java)
                        userIdglass =
                            offerSnapshot.child(Constants.USER_ID_PATH).getValue(String::class.java).toString()
                        val collectorId =
                            offerSnapshot.child(Constants.COLLECTOR_ID_PATH).getValue(String::class.java)
                        if (userId1 == collectorId) {
                            val docRef = db.collection(Constants.COLLECTORS_COLLECTION).document(userId1)
                            docRef.get()
                                .addOnSuccessListener { document ->
                                    if (document != null) {
                                        userName = document.getString("name").toString()

                                    } else {
                                        Log.d(TAG, "No such document")
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.d(TAG, "get failed with ", exception)
                                }
                            val docRef1 = db.collection(Constants.SELLERS_COLLECTION).document(userIdglass!!)
                            docRef1.get()
                                .addOnSuccessListener { document ->
                                    if (document != null) {
                                        val userName1 = document.getString("name")
                                        val text =
                                            "you $userName offered on user $userName1 to buy his quantity  of $quantity piece of glass "
                                        createCollectorTextView(text, Constants.OFFERS_GLASS_CHILD)
                                    } else {
                                        Log.d(TAG, "No such document")
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.d(TAG, "get failed with ", exception)
                                }
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    private fun checkUserPlastic() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId1 = currentUser.uid
            val offersRef = FirebaseDatabase.getInstance().reference.child(Constants.OFFERS_PLASTIC_CHILD)
            offersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (offerSnapshot in dataSnapshot.children) {
                        val quantity = offerSnapshot.child(Constants.PLASTIC_COUNT_KEY).getValue(Int::class.java)
                        userIdplastic =
                            offerSnapshot.child(Constants.USER_ID_PATH).getValue(String::class.java).toString()
                        val collectorId =
                            offerSnapshot.child(Constants.COLLECTOR_ID_PATH).getValue(String::class.java)
                        if (userId1 == collectorId) {
                            val docRef = db.collection(Constants.COLLECTORS_COLLECTION).document(userId1)
                            docRef.get()
                                .addOnSuccessListener { document ->
                                    if (document != null) {
                                        userName = document.getString("name").toString()

                                    } else {
                                        Log.d(TAG, "No such document")
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.d(TAG, "get failed with ", exception)
                                }
                            val docRef1 = db.collection(Constants.SELLERS_COLLECTION).document(userIdplastic!!)
                            docRef1.get()
                                .addOnSuccessListener { document ->
                                    if (document != null) {
                                        val userName1 = document.getString("name")
                                        val text =
                                            "you $userName offerd on user $userName1 to buy his quantity  of $quantity piece of plastic "
                                        createCollectorTextView(text, Constants.OFFERS_PLASTIC_CHILD)
                                    } else {
                                        Log.d(TAG, "No such document")
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.d(TAG, "get failed with ", exception)
                                }
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    private fun checkUserCan() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId1 = currentUser.uid
            val offersRef = FirebaseDatabase.getInstance().reference.child(Constants.OFFERS_CAN_CHILD)
            offersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (offerSnapshot in dataSnapshot.children) {
                        val quantity = offerSnapshot.child(Constants.CAN_COUNT_KEY).getValue(Int::class.java)
                        userIdcan =
                            offerSnapshot.child(Constants.USER_ID_PATH).getValue(String::class.java).toString()
                        val collectorId =
                            offerSnapshot.child(Constants.COLLECTOR_ID_PATH).getValue(String::class.java)
                        if (userId1 == collectorId) {
                            val docRef = db.collection(Constants.COLLECTORS_COLLECTION).document(userId1)
                            docRef.get()
                                .addOnSuccessListener { document ->
                                    if (document != null) {
                                        userName = document.getString("name").toString()

                                    } else {
                                        Log.d(TAG, "No such document")
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.d(TAG, "get failed with ", exception)
                                }
                            val docRef1 = db.collection(Constants.SELLERS_COLLECTION).document(userIdcan!!)
                            docRef1.get()
                                .addOnSuccessListener { document ->
                                    if (document != null) {
                                        val userName1 = document.getString("name")
                                        val text =
                                            "you $userName offerd on user $userName1 to buy his quantity  of $quantity piece of can "
                                        createCollectorTextView(text, Constants.OFFERS_CAN_CHILD)
                                    } else {
                                        Log.d(TAG, "No such document")
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.d(TAG, "get failed with ", exception)
                                }
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    private fun checkUserPackage() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId1 = currentUser.uid
            val offersRef = FirebaseDatabase.getInstance().reference.child(Constants.OFFERS_PACKAGE_CHILD)
            offersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (offerSnapshot in dataSnapshot.children) {
                        val can = offerSnapshot.child(Constants.CAN_COUNT_KEY).getValue(Int::class.java)
                        val glass = offerSnapshot.child(Constants.GLASS_COUNT_KEY).getValue(Int::class.java)
                        val plastic = offerSnapshot.child(Constants.PLASTIC_COUNT_KEY).getValue(Int::class.java)
                        val paper = offerSnapshot.child(Constants.PAPER_COUNT_KEY).getValue(Int::class.java)

                        userIdpackage =
                            offerSnapshot.child(Constants.USER_ID_PATH).getValue(String::class.java).toString()
                        val collectorId =
                            offerSnapshot.child(Constants.COLLECTOR_ID_PATH).getValue(String::class.java)
                        if (userId1 == collectorId) {
                            val docRef = db.collection(Constants.COLLECTORS_COLLECTION).document(userId1)
                            docRef.get()
                                .addOnSuccessListener { document ->
                                    if (document != null) {
                                        userName = document.getString("name").toString()

                                    } else {
                                        Log.d(TAG, "No such document")
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.d(TAG, "get failed with ", exception)
                                }
                            val docRef1 = db.collection(Constants.SELLERS_COLLECTION).document(userIdpackage!!)
                            docRef1.get()
                                .addOnSuccessListener { document ->
                                    if (document != null) {
                                        val userName1 = document.getString("name")
                                        val text =
                                            "you $userName offered on user $userName1 to buy his quantity  of $can piece of can  $plastic piece of plastic $glass piece of glass $paper piece of paper "
                                        createCollectorTextView(text, Constants.OFFERS_PACKAGE_CHILD)
                                    } else {
                                        Log.d(TAG, "No such document")
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.d(TAG, "get failed with ", exception)
                                }
                        }
                    }

                }


                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }


            })
        }
    }

    private fun checkUserPaper() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId1 = currentUser.uid
            val offersRef = FirebaseDatabase.getInstance().reference.child(Constants.OFFERS_PAPER_CHILD)
            offersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (offerSnapshot in dataSnapshot.children) {
                        val quantity = offerSnapshot.child(Constants.QUANTITY_PATH).getValue(Int::class.java)
                        userIdpaper =
                            offerSnapshot.child(Constants.USER_ID_PATH).getValue(String::class.java).toString()
                        val collectorId =
                            offerSnapshot.child(Constants.COLLECTOR_ID_PATH).getValue(String::class.java)
                        if (userId1 == collectorId) {
                            val docRef = db.collection(Constants.COLLECTORS_COLLECTION).document(userId1)
                            docRef.get()
                                .addOnSuccessListener { document ->
                                    if (document != null) {
                                        userName = document.getString("name").toString()

                                    } else {
                                        Log.d(TAG, "No such document")
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.d(TAG, "get failed with ", exception)
                                }
                            val docRef1 = db.collection(Constants.SELLERS_COLLECTION).document(userIdpaper!!)
                            docRef1.get()
                                .addOnSuccessListener { document ->
                                    if (document != null) {
                                        val userName1 = document.getString("name")
                                        val text =
                                            "you $userName offerd on user $userName1 to buy his quantity  of $quantity piece of paper "
                                        createCollectorTextView(text, Constants.OFFERS_PAPER_CHILD)
                                    } else {
                                        Log.d(TAG, "No such document")
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.d(TAG, "get failed with ", exception)
                                }
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }
}







