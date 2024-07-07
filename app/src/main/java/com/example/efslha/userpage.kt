package com.example.efslha

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.efslha.databinding.ActivityUserpageBinding
import com.example.efslha.ui.main.Feedback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class userpage : AppCompatActivity() {
    private lateinit var sp: SharedPreferences
    lateinit var firestore1: FirebaseFirestore
    lateinit var auth: FirebaseAuth

    var counterseparate = 0
    var counterpackagesepplastic = 0
    var counterpackagesepcan = 0
    var counterpackageseppaper = 0
    var counterpackagesepglass = 0
    lateinit var database: FirebaseDatabase
    private lateinit var databaseRef1: DatabaseReference
    private lateinit var databaseRef2: DatabaseReference
    private lateinit var databaseRef3: DatabaseReference
    private lateinit var databaseRef4: DatabaseReference
    private lateinit var logout:Button

    var counter = 0
    var countercan = 0
    var counterglass = 0
    var counterpaper = 0
    var counterplastic = 0

  private fun logout (){
      FirebaseAuth.getInstance().signOut()
      val intent = Intent(this,LoginSignupActivity::class.java)
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
      startActivity(intent)
      finish()
  }
    fun restartApp() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        )
        startActivity(intent)
        finish()
    }
    private lateinit var binding: ActivityUserpageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserpageBinding.inflate(layoutInflater)
        sp = getSharedPreferences(Constants.USER_DATA, MODE_PRIVATE)
        binding.collectorLogout.setOnClickListener {
            logout()

        }

        binding.apply {

            setContentView(root)
            auth = FirebaseAuth.getInstance()
            val firebaseAuth = FirebaseAuth.getInstance()
            firestore1 = FirebaseFirestore.getInstance()
            database = FirebaseDatabase.getInstance()
            databaseRef1 = database.getReference(Constants.SELLERS_PACKAGE)
            databaseRef2 = database.getReference(Constants.SELLERS_SEPARATE)
            databaseRef3 = database.getReference("sellerspackagelater")
            databaseRef4 = database.getReference("sellers separate later")


            requestlater.setOnClickListener {
                uploadDataLater()
                val intent = Intent(this@userpage, UnconfirmedOrder::class.java)
                intent.putExtra("userid", auth.currentUser?.uid)
                startActivity(intent)
            }
            requestNowBtn.setOnClickListener {
                uploadData()
            }
            plastic.setOnClickListener {
                counterseparate += 1 // Incrementing by 1
                counterplastic += 3 // Incrementing by 3
                counter += 3 // Incrementing by 3
                counterpackagesepplastic += 1 // Incrementing by 1
                totalPointsTv.text =
                    if (counter <= 0) "0" else counter.toString() // Update the UI with the new counter value
            }

            plasticminus.setOnClickListener {
                if (counterplastic > 0) {
                    counter -= 3
                    counterplastic -= 3
                    counterpackagesepplastic -= 1
                }
                totalPointsTv.text = if (counter <= 0) "0" else counter.toString()
            }


            paper.setOnClickListener {
                counterseparate += 1 // Incrementing by 1
                counterpaper += 1 // Incrementing by 1
                counter += 1 // Incrementing by 1
                counterpackageseppaper += 1 // Incrementing by 1
                totalPointsTv.text =
                    if (counter <= 0) "0" else counter.toString() // Update the UI with the new counter value
            }

            paperminus.setOnClickListener {
                if (counterpaper > 0) {
                    counter -= 1
                    counterpaper -= 1
                    counterpackageseppaper -= 1
                }
                totalPointsTv.text = if (counter <= 0) "0" else counter.toString()
            }


            glass.setOnClickListener {
                counterseparate += 1 // Incrementing by 1
                counterglass += 4 // Incrementing by 4
                counter += 4 // Incrementing by 4
                counterpackagesepglass += 1 // Incrementing by 1
                totalPointsTv.text =
                    if (counter <= 0) "0" else counter.toString() // Update the UI with the new counter value
            }


            glassminus.setOnClickListener {
                if (counterglass > 0) {
                    counter -= 4
                    counterglass -= 4
                    counterpackagesepglass -= 1
                }
                totalPointsTv.text = if (counter <= 0) "0" else counter.toString()
            }

            can.setOnClickListener {
                counterseparate += 1 // Incrementing by 1
                countercan += 6 // Incrementing by 6
                counter += 6 // Incrementing by 6
                counterpackagesepcan += 1 // Incrementing by 1
                totalPointsTv.text =
                    if (counter <= 0) "0" else counter.toString() // Update the UI with the new counter value
            }

            canminus.setOnClickListener {
                if (countercan > 0) {
                    counter -= 6
                    countercan -= 6
                    counterpackagesepcan -= 1
                }
                totalPointsTv.text = if (counter <= 0) "0" else counter.toString()
            }


            orderHistoryBtn.setOnClickListener {
                counterseparate += 1
                val intent = Intent(this@userpage, OrderHistory::class.java)
                intent.putExtra("counterdata", counterseparate)
                startActivity(intent)
            }
            val currentUser = firebaseAuth.currentUser
            val uid = currentUser?.uid
            if (uid != null) {
                firestore1.collection(Constants.SELLERS_COLLECTION).document(uid).get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            val userData = documentSnapshot.data
                            var userName = userData?.get("name") as? String
                            if (userName != null) {
                                name.text = "Hello $userName"
                            }
                        } else {
                            name.text = "name is not available, try again"
                        }
                    }
//
            }
        }

    }

    fun messagecenter(view: View) {
        val a = Intent(this@userpage, Feedback::class.java)
        startActivity(a)
    }

    fun help(view: View) {
        val a = Intent(this@userpage, Help1::class.java)
        startActivity(a)
    }

    fun wallet(view: View) {
        val a = Intent(this@userpage, WalletActivity::class.java)
        startActivity(a)
    }


    fun wallet1(view: View) {
        val a = Intent(this@userpage, UnconfirmedOrder::class.java)
        startActivity(a)
    }

    fun requestnow(view: View) {
        val a = Intent(this@userpage, RequestNow::class.java)
        startActivity(a)
    }

    override fun onDestroy() {
        // Reset the counter to 0
        counter = 0
        counterplastic = 0
        countercan = 0
        counterglass = 0
        counterpaper = 0
        counterseparate = 0

        super.onDestroy()
    }

    private fun uploadData() {
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        val userId = currentUser?.uid

        userId?.let { uid ->

            if ((counterplastic > 0 && (countercan > 0 || counterglass > 0 || counterpaper > 0)) ||
                (countercan > 0 && (counterplastic > 0 || counterglass > 0 || counterpaper > 0)) ||
                (counterglass > 0 && (counterplastic > 0 || countercan > 0 || counterpaper > 0)) ||
                (counterpaper > 0 && (counterplastic > 0 || countercan > 0 || counterglass > 0))
            ) {
                //PACKAGE
                databaseRef1.child(Constants.SELLERS_CHILD).child(uid).get()
                    .addOnSuccessListener { dataSnapshot ->
                        if (!dataSnapshot.exists()) {
                            val userData = mapOf(
                                Constants.PLASTIC_COUNT_KEY to counterpackagesepplastic,
                                Constants.GLASS_COUNT_KEY to counterpackagesepglass,
                                Constants.CAN_COUNT_KEY to counterpackagesepcan,
                                Constants.PAPER_COUNT_KEY to counterpackageseppaper,
                                Constants.SELLER_NAME_PATH to sp.getString(Constants.SELLER_NAME_PATH,null),
                                Constants.POINTS_CHILD to binding.totalPointsTv.text

                            )

                            databaseRef1.child(Constants.SELLERS_CHILD).child(uid).setValue(userData)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this@userpage,
                                        "Order Made successfully",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    val intent = Intent(this@userpage, RequestNow::class.java)
                                    intent.putExtra("userid", auth.currentUser?.uid)
                                    startActivity(intent)

                                    resetCounters()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this@userpage, "Data upload failed", Toast.LENGTH_LONG)
                                        .show()
                                }
                        }
                        else {
                            Toast.makeText(
                                this@userpage,
                                "Data already exists in package",
                                Toast.LENGTH_LONG
                            ).show()
                            resetCounters()
                        }
                    }
                    .addOnFailureListener { exception ->
                        // Handle any errors that occur during the database operation
                        Toast.makeText(
                            this@userpage,
                            "Failed to check data in sellers package",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            } else {
                val selectedCategory = when {
                    counterplastic > 0 -> Constants.PLASTIC_OFFER_TYPE
                    countercan > 0 -> Constants.CAN_OFFER_TYPE
                    counterglass > 0 -> Constants.GLASS_OFFER_TYPE
                    counterpaper > 0 -> Constants.PAPER_OFFER_TYPE
                    else -> ""
                }
                val childCountKey = when {
                    counterplastic > 0 -> Constants.PLASTIC_COUNT_KEY
                    countercan > 0 -> Constants.CAN_COUNT_KEY
                    counterglass > 0 -> Constants.GLASS_COUNT_KEY
                    counterpaper > 0 -> Constants.PAPER_COUNT_KEY
                    else -> ""

                }

                if (selectedCategory.isNotEmpty() && childCountKey.isNotEmpty()) {
                    // Check if data already exists in the Constants.SELLERS_INDIVIDUAL package
                    databaseRef2.child(Constants.SELLERS_INDIVIDUAL).child(uid)
                        .get() // Corrected the reference here
                        .addOnSuccessListener { dataSnapshot ->
                            if (!dataSnapshot.exists()) {
                                // Data doesn't exist, upload new data to the Constants.SELLERS_INDIVIDUAL package
                                val userData = mapOf(
                                    childCountKey to counterseparate,
                                    Constants.OFFER_TYPE to selectedCategory,
                                    Constants.SELLER_NAME_PATH to sp.getString(Constants.SELLER_NAME_PATH,null),

                                    Constants.POINTS_CHILD to binding.totalPointsTv.text
                                )

                                databaseRef2.child(Constants.SELLERS_INDIVIDUAL).child(uid)
                                    .setValue(userData)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this@userpage,
                                            "Order Made successfully",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        val intent = Intent(this@userpage, RequestNow::class.java)
                                        intent.putExtra("userid", auth.currentUser?.uid)
                                        startActivity(intent)

                                        resetCounters() // Reset counters after successful upload
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            this@userpage,
                                            "Data upload failed",
                                            Toast.LENGTH_LONG
                                        )
                                            .show()
                                    }
                            } else {
                                // Data already exists in sellers individual package
                                Toast.makeText(
                                    this@userpage,
                                    "Data already exists in  individual package ",
                                    Toast.LENGTH_LONG
                                ).show()
                                resetCounters()
                            }
                        }.addOnFailureListener { exception ->
                            // Handle any errors that occur during the database operation
                            Toast.makeText(
                                this@userpage,
                                "Failed to check data in sellers individual",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
                else {
                    Toast.makeText(this@userpage, "Please select a category", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@userpage, RequestNow::class.java)
                    intent.putExtra("userid", auth.currentUser?.uid)
                    startActivity(intent)
                }
            }
        }


    }

    private fun uploadDataLater() {
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        val userId = currentUser?.uid

        userId?.let { uid ->
            if ((counterplastic > 0 && (countercan > 0 || counterglass > 0 || counterpaper > 0)) ||
                (countercan > 0 && (counterplastic > 0 || counterglass > 0 || counterpaper > 0)) ||
                (counterglass > 0 && (counterplastic > 0 || countercan > 0 || counterpaper > 0)) ||
                (counterpaper > 0 && (counterplastic > 0 || countercan > 0 || counterglass > 0))
            ) {
                databaseRef3.child("sellers later").child(uid).get()
                    .addOnSuccessListener { dataSnapshot ->
                        if (!dataSnapshot.exists()) {
                            // Data doesn't exist, upload new data
                            val userData = mapOf(
                                Constants.PLASTIC_COUNT_KEY to counterpackagesepplastic,
                               Constants.GLASS_COUNT_KEY to counterpackagesepglass,
                               Constants.CAN_COUNT_KEY to counterpackagesepcan,
                                Constants.PAPER_COUNT_KEY to counterpackageseppaper
                            )

                            databaseRef3.child("sellers later").child(uid).setValue(userData)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this@userpage,
                                        "Data uploaded to sellers package later",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    resetCounters() // Reset counters after successful upload
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this@userpage, "Data upload failed", Toast.LENGTH_LONG)
                                        .show()
                                }
                        } else {
                            Toast.makeText(
                                this@userpage,
                                "Data already exists in sellers package later",
                                Toast.LENGTH_LONG
                            ).show()
                            resetCounters()

                        }
                    }.addOnFailureListener { exception ->
                        // Handle any errors that occur during the database operation
                        Toast.makeText(
                            this@userpage,
                            "Failed to check data in sellers package later",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            } else {
                val selectedCategory = when {
                    counterplastic > 0 -> Constants.PLASTIC_COUNT_KEY
                    countercan > 0 -> Constants.CAN_COUNT_KEY
                    counterglass > 0 -> Constants.GLASS_COUNT_KEY
                    counterpaper > 0 -> Constants.PAPER_COUNT_KEY
                    else -> ""
                }

                if (!selectedCategory.isEmpty()) {
                    // Check if data already exists in the Constants.SELLERS_INDIVIDUAL package
                    databaseRef4.child("sellers individual later").child(uid)
                        .get() // Corrected the reference here
                        .addOnSuccessListener { dataSnapshot ->
                            if (!dataSnapshot.exists()) {
                                // Data doesn't exist, upload new data to the Constants.SELLERS_INDIVIDUAL package
                                val userData = mapOf(
                                    selectedCategory to counterseparate,
                                    Constants.OFFER_TYPE to selectedCategory,
                                    Constants.SELLER_NAME_PATH to sp.getString(Constants.SELLER_NAME_PATH,null)
                                )

                                databaseRef4.child("sellers individual later").child(uid)
                                    .setValue(userData)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this@userpage,
                                            "Data uploaded to individual package later",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        resetCounters() // Reset counters after successful upload
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            this@userpage,
                                            "Data upload failed",
                                            Toast.LENGTH_LONG
                                        )
                                            .show()
                                    }
                            } else {
                                // Data already exists in sellers individual package
                                Toast.makeText(
                                    this@userpage,
                                    "Data already exists in sellers individual later",
                                    Toast.LENGTH_LONG
                                ).show()
                                resetCounters()

                            }
                        }.addOnFailureListener { exception ->
                            // Handle any errors that occur during the database operation
                            Toast.makeText(
                                this@userpage,
                                "Failed to check data in sellers individual",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                } else {
                    // No category selected
                    Toast.makeText(this@userpage, "Please select a category", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    override fun onPause() {
        super.onPause()
//        resetCounters()
    }
    private fun resetCounters() {
        counter = 0
        counterplastic = 0
        countercan = 0
        counterglass = 0
        counterpaper = 0
        counterseparate = 0
        counterpackagesepcan = 0
        counterpackagesepglass = 0
        counterpackageseppaper = 0
        counterpackagesepplastic = 0
        binding.totalPointsTv.text = (0).toString()
    }

    fun feedback(view: View) {
        val a=Intent(this@userpage,Feedback::class.java)
        startActivity(a)
    }

}






