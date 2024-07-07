package com.example.efslha

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.efslha.databinding.ActivityCollectionCodeBinding
import com.example.efslha.ui.main.Feedback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class CollectionCodeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCollectionCodeBinding
    private var sellerId: String? = null
    private lateinit var db: FirebaseFirestore
    private lateinit var code: String
    private lateinit var collectorId: String
    private lateinit var realtimeDb: FirebaseDatabase
    private var sellerDocumentRef: DocumentReference? = null
    private var canCount: Int = 0
    private var plasticCount: Int = 0
    private var glassCount: Int = 0
    private var paperCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollectionCodeBinding.inflate(layoutInflater)
        sellerId = intent.getStringExtra(Constants.USER_ID_KEY)
        plasticCount = intent.getIntExtra(Constants.PLASTIC_COUNT_KEY, 0)
        canCount = intent.getIntExtra(Constants.CAN_COUNT_KEY, 0)
        glassCount = intent.getIntExtra(Constants.GLASS_COUNT_KEY, 0)
        paperCount = intent.getIntExtra(Constants.PAPER_COUNT_KEY, 0)
        collectorId = FirebaseAuth.getInstance().uid ?: ""
        db = FirebaseFirestore.getInstance()
        realtimeDb = FirebaseDatabase.getInstance()
        if (collectorId.isEmpty()) {
            Toast.makeText(
                this@CollectionCodeActivity,
                "Can't load collector data",
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

        sellerDocumentRef =
            if (sellerId.isNullOrEmpty()) null else db.collection(Constants.SELLERS_COLLECTION)
                .document(sellerId!!)

        binding.apply {
            setContentView(root)

            submitBtn.setOnClickListener { submit() }
        }
    }

    private fun submit() {
        if (binding.codeEt.text.isEmpty()) {
            binding.codeEt.error = "Please, enter the collection code"
        } else if (!sellerId.isNullOrEmpty()) {
            binding.codeEt.error = null
            sellerDocumentRef?.get()?.addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    code = task.result.getString(Constants.GENERATED_CODE_FIELD) ?: ""
                    if (code.isEmpty()) {
                        Toast.makeText(
                            this@CollectionCodeActivity,
                            "Can't load seller data",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                        return@addOnCompleteListener
                    }
                    if (binding.codeEt.text.toString() == code) {
                        binding.codeEt.error = null
                        realtimeDb.reference.child(Constants.SELLERS_STATUS_PATH)
                            .get().addOnCompleteListener {
                                if (it.isSuccessful && it.result != null) {
                                    for (child in it.result.children) {
                                        val sellerID = child.child(Constants.USER_ID_PATH).value?.toString() ?: ""
                                        val collectorID = child.child(Constants.COLLECTOR_ID_PATH).value?.toString() ?: ""
                                        if (sellerId == sellerID && collectorId == collectorID) {
                                            val canNumber = child.child(Constants.CAN_COUNT_KEY).value?.toString()?.toInt() ?: 0
                                            val plasticNumber = child.child(Constants.PLASTIC_COUNT_KEY).value?.toString()?.toInt() ?: 0
                                            val glassNumber = child.child(Constants.GLASS_COUNT_KEY).value?.toString()?.toInt() ?: 0
                                            val paperNumber = child.child(Constants.PAPER_COUNT_KEY).value?.toString()?.toInt() ?: 0
                                            if (requestType(canCount, paperCount, plasticCount, glassCount) == requestType(canNumber, glassNumber, plasticNumber, paperNumber)) {
                                                realtimeDb.reference.child(Constants.SELLERS_STATUS_PATH).child(child.key!!).removeValue()
                                                    .addOnSuccessListener {
                                                        val oldPoints = task.result.getLong(Constants.POINTS_FIELD) ?: 0
                                                        val newPoints = oldPoints + (canCount * Constants.CAN_VALUE + plasticCount * Constants.PLASTIC_VALUE + glassCount * Constants.GLASS_VALUE + paperCount * Constants.PAPER_VALUE)
                                                        val updateMap = hashMapOf<String, Any>(Constants.POINTS_FIELD to newPoints)
                                                        db.collection(Constants.SELLERS_COLLECTION).document(sellerId!!).update(updateMap).addOnCompleteListener { updatingTask ->
                                                            if (updatingTask.isSuccessful) {
                                                                val a = Intent(this@CollectionCodeActivity, donedeal::class.java)
                                                                startActivity(a)
                                                                finish()
                                                            } else {
                                                                Toast.makeText(this@CollectionCodeActivity, updatingTask.exception?.localizedMessage ?: "Unknown error", Toast.LENGTH_SHORT).show()
                                                            }
                                                        }
                                                    }.addOnFailureListener {
                                                        Toast.makeText(this@CollectionCodeActivity, it.localizedMessage ?: "Unknown error", Toast.LENGTH_SHORT).show()
                                                    }
                                            }
                                        }
                                    }
                                } else {
                                    Toast.makeText(this@CollectionCodeActivity, it.exception?.localizedMessage ?: "Unknown error", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        binding.codeEt.error = "This code is wrong"
                    }
                } else {
                    Toast.makeText(this@CollectionCodeActivity, task.exception?.localizedMessage ?: "Unknown error", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        } else {
            db.collection(Constants.SELLERS_COLLECTION).get().addOnCompleteListener {
                if (it.isSuccessful && it.result != null) {
                    var documentSnapshot: DocumentSnapshot? = null
                    for (documentSnap in it.result) {
                        if (documentSnap.getString(Constants.GENERATED_CODE_FIELD) == binding.codeEt.text.toString()) {
                            documentSnapshot = documentSnap
                            break
                        }
                    }
                    if (documentSnapshot != null) {
                        realtimeDb.reference.child(Constants.SELLERS_SEPARATE).child(Constants.SELLERS_INDIVIDUAL).get().addOnCompleteListener { task ->
                            if (task.isSuccessful && task.result != null) {
                                var childReference: DatabaseReference? = null
                                for (child in task.result.children) {
                                    if (child.key == documentSnapshot.id) {
                                        childReference = child.ref
                                        break
                                    }
                                }
                                if (childReference != null) {
                                    applyPoints(childReference, documentSnapshot, task.result.child(documentSnapshot.id).child(Constants.POINTS_CHILD).value.toString().toInt())
                                } else {
                                    realtimeDb.reference.child(Constants.SELLERS_PACKAGE).child(Constants.SELLERS_CHILD).get().addOnCompleteListener { sellersTask ->
                                        if (sellersTask.isSuccessful && sellersTask.result != null) {
                                            var childRef: DatabaseReference? = null
                                            for (child in sellersTask.result.children) {
                                                if (child.key == documentSnapshot.id) {
                                                    childRef = child.ref
                                                    break
                                                }
                                            }
                                            if (childRef != null) {
                                                applyPoints(childRef, documentSnapshot, sellersTask.result.child(documentSnapshot.id).child(Constants.POINTS_CHILD).value.toString().toInt())
                                            }
                                        } else {
                                            Toast.makeText(this@CollectionCodeActivity, sellersTask.exception?.localizedMessage ?: "Unknown error", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            } else {
                                realtimeDb.reference.child(Constants.SELLERS_PACKAGE).child(Constants.SELLERS_CHILD).get().addOnCompleteListener { sellersTask ->
                                    if (sellersTask.isSuccessful && sellersTask.result != null) {
                                        var sellerReference: DatabaseReference? = null
                                        for (child in sellersTask.result.children) {
                                            if (child.key == documentSnapshot.id) {
                                                sellerReference = child.ref
                                                break
                                            }
                                        }
                                        if (sellerReference != null) {
                                            applyPoints(sellerReference, documentSnapshot, sellersTask.result.child(documentSnapshot.id).child(Constants.POINTS_CHILD).value.toString().toInt())
                                        }
                                    } else {
                                        Toast.makeText(this@CollectionCodeActivity, sellersTask.exception?.localizedMessage ?: "Unknown error", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this@CollectionCodeActivity, "No one has this code", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@CollectionCodeActivity, it.exception?.localizedMessage ?: "Unknown error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun requestType(
        canCount: Int,
        paperCount: Int,
        plasticCount: Int,
        glassCount: Int
    ): String {
        var positive = 0
        if (canCount > 0) {
            positive++
        }
        if (paperCount > 0) {
            positive++
        }
        if (plasticCount > 0)
            positive++
        if (glassCount > 0)
            positive++
        return if (positive > 1)
            Constants.PACKAGE_REQ
        else
            Constants.SEPARATE_REQ
    }

    private fun applyPoints(
        databaseReference: DatabaseReference,
        documentSnapshot: DocumentSnapshot,
        points: Int = -1
    ) {
        val oldPoints =
            documentSnapshot.getLong(Constants.POINTS_FIELD)
                ?: 0
        val newPoints = oldPoints +
                if (points == -1) {
                    (canCount * Constants.CAN_VALUE +
                            plasticCount * Constants.PLASTIC_VALUE +
                            glassCount * Constants.GLASS_VALUE +
                            paperCount * Constants.PAPER_VALUE
                            )
                } else points
        val updateMap = hashMapOf<String, Any>(
            Constants.POINTS_FIELD to newPoints,
        )
        db.collection(Constants.SELLERS_COLLECTION)
            .document(documentSnapshot.id)
            .update(updateMap)
            .addOnCompleteListener { updatingTask ->
                if (updatingTask.isSuccessful) {
                    databaseReference.removeValue().addOnCompleteListener { removingTask ->
                        if (removingTask.isSuccessful) {
                            val a = Intent(this@CollectionCodeActivity, donedeal::class.java)
                            startActivity(a)
                            Toast.makeText(this@CollectionCodeActivity, "Transaction has been completed successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@CollectionCodeActivity, removingTask.exception?.localizedMessage ?: "Unknown error", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this@CollectionCodeActivity, updatingTask.exception?.localizedMessage ?: "Unknown error", Toast.LENGTH_SHORT).show()
                }
            }
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
}
