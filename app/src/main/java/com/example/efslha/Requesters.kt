package com.example.efslha

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat

import com.example.efslha.databinding.ActivityRequestersBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore

class Requesters : AppCompatActivity() {
    private lateinit var binding: ActivityRequestersBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private val dbRealtime = Firebase.database.reference
    private var currentUserID = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        checkUserGlass()
        checkUserPaper()
        checkUserPlastic()
        checkUserCan()
        checkUserPackage()
    }


    private fun createCollectorTextView(
        collectorName: String?,
        canCount: Int,
        glassCount: Int,
        plasticCount: Int,
        paperCount: Int,
        collectorId: String,
        requestType: String,
        path: String
    ) {
        val newTextView = TextView(this)
        var data = "$collectorName requests to buy your offer of"
        if (canCount>0)
            data += " can quantity : $canCount,"
        if (plasticCount>0)
            data += " plastic quantity : $plasticCount,"
        if (glassCount>0)
            data += " glass quantity : $glassCount,"
        if (paperCount>0)
            data += " paper quantity : $paperCount,"
        data = if (data.get(data.lastIndex) == ',') data.substring(0,data.lastIndex) else data
        newTextView.text = data
        newTextView.setBackgroundResource(R.drawable.plaintextfigure)
        newTextView.typeface = ResourcesCompat.getFont(this, R.font.inter_semibold)
        newTextView.gravity = Gravity.CENTER
        newTextView.tag = collectorId
        newTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        val widthInPixels = dpToPx(344)
        val heightInPixels = dpToPx(70)
        newTextView.setOnClickListener {
            val docRef = firestore.collection(Constants.SELLERS_COLLECTION).document(currentUserID)
            docRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    val name = document.getString("name")
                    val phone = document.getString("phone")
                    val userData = mapOf(
                        "name" to name,
                        "phone" to phone,
                        "status" to true,
                        Constants.USER_ID_PATH to currentUserID,
                        Constants.COLLECTOR_ID_PATH to collectorId,
                        Constants.CAN_COUNT_KEY to canCount,
                        Constants.GLASS_COUNT_KEY to glassCount,
                        Constants.PLASTIC_COUNT_KEY to plasticCount,
                        Constants.PAPER_COUNT_KEY to paperCount
                    )
                    dbRealtime.child(Constants.SELLERS_STATUS_PATH).push()
                        .setValue(userData)
                        .addOnSuccessListener {
                            dbRealtime.child(path).get()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful && task.result != null) {
                                        var isExisted = false
                                        for (child in task.result.children) {
                                            if (child.child(Constants.USER_ID_PATH).value.toString() == currentUserID && child.child(Constants.COLLECTOR_ID_PATH).value.toString() == collectorId) {
                                                isExisted = true
                                                child.ref.removeValue().addOnCompleteListener {
                                                    if (task.isSuccessful && task.result != null) {
                                                        if (requestType == Constants.SEPARATE_REQ) {
                                                            // SEPARATE REQ
                                                            database.child(Constants.SELLERS_SEPARATE)
                                                                .child(Constants.SELLERS_INDIVIDUAL)
                                                                .child(currentUserID)
                                                                .removeValue()
                                                                .addOnCompleteListener {
                                                                    if (it.isSuccessful) {
                                                                        binding.requestsLayout.findViewWithTag<View?>(collectorId)?.let {
                                                                                view->
                                                                            binding.requestsLayout.removeView(view)
                                                                        }

                                                                        Toast.makeText(
                                                                            this@Requesters,
                                                                            "You have accepted this offer from $collectorName ",
                                                                            Toast.LENGTH_LONG
                                                                        ).show()
                                                                    } else                                                                         Toast.makeText(
                                                                        this@Requesters,
                                                                        it.exception?.localizedMessage?:"Unknown error occurred",
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()

                                                                }
                                                        } else {
                                                            // PACKAGE REQ
                                                            database.child(Constants.SELLERS_PACKAGE)
                                                                .child(Constants.SELLERS_CHILD)
                                                                .child(currentUserID)
                                                                .removeValue()
                                                                .addOnCompleteListener {
                                                                    if (it.isSuccessful) {
                                                                        binding.requestsLayout.findViewWithTag<View?>(collectorId)?.let {
                                                                                view->
                                                                            binding.requestsLayout.removeView(view)
                                                                        }


                                                                        Toast.makeText(
                                                                            this@Requesters,
                                                                            "You have accepted this offer from $collectorName ",
                                                                            Toast.LENGTH_LONG
                                                                        ).show()
                                                                    } else
                                                                        Toast.makeText(
                                                                            this@Requesters,
                                                                            it.exception?.localizedMessage?:"Unknown error occurred",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                }

                                                        }
                                                    } else {
                                                        Toast.makeText(this@Requesters, task.exception?.localizedMessage?:"Unknown error occurred", Toast.LENGTH_SHORT)
                                                            .show()
                                                    }
                                                }
                                                break
                                                return@addOnCompleteListener
                                            }
                                        }
                                        if (!isExisted)
                                            Toast.makeText(this@Requesters, "Can't find the offer in database", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(this@Requesters, "", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                        .addOnFailureListener { exception ->
                            Log.d(TAG, "Error saving data: ", exception)
                            Toast.makeText(
                                applicationContext,
                                "Failed to save data: ${exception.localizedMessage}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                } else {
                    Log.d(TAG, "No such document")
                    Toast.makeText(applicationContext, "No such document", Toast.LENGTH_SHORT)
                        .show()
                }
            }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                    Toast.makeText(
                        applicationContext,
                        "Error getting document: ${exception.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

        }

        val layoutParams = LinearLayout.LayoutParams(widthInPixels, heightInPixels)
        layoutParams.gravity = Gravity.CENTER
        val marginInPixels = dpToPx(24)
        layoutParams.setMargins(marginInPixels, marginInPixels, marginInPixels, marginInPixels)

        newTextView.layoutParams = layoutParams

        binding.requestsLayout.addView(newTextView)
    }

    private fun dpToPx(dp: Int): Int {
        val scale = resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    private fun checkUserGlass() {
        database.child(Constants.OFFERS_GLASS_CHILD)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (offerSnapshot in snapshot.children) {
                        val sellerId =
                            offerSnapshot.child(Constants.USER_ID_PATH).getValue(String::class.java)
                                ?: ""
                        if (sellerId.isEmpty()) {
                            Toast.makeText(
                                this@Requesters,
                                "Error while loading data",
                                Toast.LENGTH_SHORT
                            ).show()
                            return
                        }
                        val currentUserId = auth.currentUser?.uid
                        if (sellerId == currentUserId) {
                            val collectorId = offerSnapshot.child(Constants.COLLECTOR_ID_PATH)
                                .getValue(String::class.java)
                            val quantity = offerSnapshot.child(Constants.GLASS_COUNT_KEY)
                                .getValue(Int::class.java)
                            collectorId?.let {
                                firestore.collection(Constants.COLLECTORS_COLLECTION).document(it)
                                    .get()
                                    .addOnSuccessListener { userDocument ->
                                        if (userDocument.exists()) {
                                            val collectorName = userDocument.getString("name")
                                            createCollectorTextView(
                                                collectorName,
                                                0,
                                                quantity?:0,
                                                0,
                                                0,
                                                collectorId,
                                                Constants.SEPARATE_REQ,
                                                Constants.OFFERS_GLASS_CHILD
                                            )
                                        } else {
                                            Log.d("requesters", "No such document")
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.d("requesters", "get failed with ", exception)
                                    }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
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

    private fun checkUserPaper() {
        database.child(Constants.OFFERS_PAPER_CHILD)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (offerSnapshot in snapshot.children) {
                        val sellerId =
                            offerSnapshot.child(Constants.USER_ID_PATH).getValue(String::class.java)
                                ?: ""
                        if (sellerId.isEmpty()) {
                            Toast.makeText(
                                this@Requesters,
                                "Error while loading some data",
                                Toast.LENGTH_SHORT
                            ).show()
                            return
                        }
                        val currentUserId = auth.currentUser?.uid
                        if (sellerId == currentUserId) {
                            val collectorId = offerSnapshot.child(Constants.COLLECTOR_ID_PATH)
                                .getValue(String::class.java)
                            val quantity = offerSnapshot.child(Constants.PAPER_COUNT_KEY)
                                .getValue(Int::class.java)
                            collectorId?.let {
                                firestore.collection(Constants.COLLECTORS_COLLECTION).document(it)
                                    .get()
                                    .addOnSuccessListener { userDocument ->
                                        if (userDocument.exists()) {
                                            val collectorName = userDocument.getString("name")
                                            createCollectorTextView(
                                                collectorName,
                                                0,
                                                0,
                                                0,
                                                quantity?:0,
                                                collectorId,
                                                Constants.SEPARATE_REQ,
                                                Constants.OFFERS_PAPER_CHILD
                                            )
                                        } else {
                                            Log.d("requesters", "No such document")
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.d("requesters", "get failed with ", exception)
                                    }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun checkUserPlastic() {
        database.child(Constants.OFFERS_PLASTIC_CHILD)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (offerSnapshot in snapshot.children) {
                        val sellerId =
                            offerSnapshot.child(Constants.USER_ID_PATH).getValue(String::class.java)
                                ?: ""
                        if (sellerId.isEmpty()) {
                            Toast.makeText(
                                this@Requesters,
                                "Error while loading some data",
                                Toast.LENGTH_SHORT
                            ).show()
                            return
                        }
                        val currentUserId = auth.currentUser?.uid
                        if (sellerId == currentUserId) {
                            val collectorId = offerSnapshot.child(Constants.COLLECTOR_ID_PATH)
                                .getValue(String::class.java)
                            val quantity = offerSnapshot.child(Constants.PLASTIC_COUNT_KEY)
                                .getValue(Int::class.java)
                            collectorId?.let {
                                firestore.collection(Constants.COLLECTORS_COLLECTION).document(it)
                                    .get()
                                    .addOnSuccessListener { userDocument ->
                                        if (userDocument.exists()) {
                                            val collectorName = userDocument.getString("name")
                                            createCollectorTextView(
                                                collectorName,
                                                0,
                                                0,
                                                quantity?:0,
                                                0,
                                                collectorId,
                                                Constants.SEPARATE_REQ,
                                                Constants.OFFERS_PLASTIC_CHILD
                                            )
                                        } else {
                                            Log.d("requesters", "No such document")
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.d("requesters", "get failed with ", exception)
                                    }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun checkUserCan() {
        database.child(Constants.OFFERS_CAN_CHILD)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (offerSnapshot in snapshot.children) {
                        val sellerId =
                            offerSnapshot.child(Constants.USER_ID_PATH).getValue(String::class.java)
                                ?: ""
                        if (sellerId.isEmpty()) {
                            Toast.makeText(
                                this@Requesters,
                                "Error while loading some data",
                                Toast.LENGTH_SHORT
                            ).show()
                            return
                        }
                        val currentUserId = auth.currentUser?.uid
                        if (sellerId == currentUserId) {
                            val collectorId = offerSnapshot.child(Constants.COLLECTOR_ID_PATH)
                                .getValue(String::class.java)
                            val quantity = offerSnapshot.child(Constants.CAN_COUNT_KEY)
                                .getValue(Int::class.java)
                            collectorId?.let {
                                firestore.collection(Constants.COLLECTORS_COLLECTION).document(it)
                                    .get()
                                    .addOnSuccessListener { userDocument ->
                                        if (userDocument.exists()) {
                                            val collectorName = userDocument.getString("name")
                                            createCollectorTextView(
                                                collectorName,
                                                quantity?:0,
                                                0,
                                                0,
                                                0,
                                                collectorId,
                                                Constants.SEPARATE_REQ,
                                                Constants.OFFERS_CAN_CHILD
                                            )
                                        } else {
                                            Log.d("requesters", "No such document")
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.d("requesters", "get failed with ", exception)
                                    }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun checkUserPackage() {
        database.child(Constants.OFFERS_PACKAGE_CHILD)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (offerSnapshot in snapshot.children) {
                        val sellerId =
                            offerSnapshot.child(Constants.USER_ID_PATH).getValue(String::class.java)
                                ?: ""
                        val currentUserId = auth.currentUser?.uid ?: ""
                        if (sellerId == currentUserId && currentUserId.isNotEmpty() && sellerId.isNotEmpty()) {
                            val collectorId = offerSnapshot.child(Constants.COLLECTOR_ID_PATH)
                                .getValue(String::class.java)
                            val quantity = offerSnapshot.child(Constants.CAN_COUNT_KEY).getValue(Int::class.java)
                            val quantity1 = offerSnapshot.child(Constants.GLASS_COUNT_KEY).getValue(Int::class.java)
                            val quantity2 = offerSnapshot.child(Constants.PAPER_COUNT_KEY).getValue(Int::class.java)
                            val quantity3 = offerSnapshot.child(Constants.PLASTIC_COUNT_KEY).getValue(Int::class.java)



                            collectorId?.let {
                                firestore.collection(Constants.COLLECTORS_COLLECTION).document(it)
                                    .get()
                                    .addOnSuccessListener { userDocument ->
                                        if (userDocument.exists()) {
                                            val collectorName = userDocument.getString("name")
                                            createCollectorTextView(
                                                collectorName,
                                                quantity?:0,
                                                quantity1?:0,
                                                quantity3?:0,
                                                quantity2?:0,
                                                collectorId,
                                                Constants.PACKAGE_REQ,
                                                Constants.OFFERS_PACKAGE_CHILD
                                            )

                                        } else {
                                            Log.d("requesters", "No such document")
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.d("requesters", "get failed with ", exception)
                                    }
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
