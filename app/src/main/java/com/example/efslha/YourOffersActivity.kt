package com.example.efslha

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.efslha.databinding.ActivityYourOffersBinding
import com.example.efslha.ui.main.Feedback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class YourOffersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityYourOffersBinding
    private lateinit var adapter: OffersAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var database: FirebaseDatabase
    private var offers = ArrayList<Offer>()
    private var collectorId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYourOffersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = OffersAdapter(this, offers) {
            val intent = Intent(this@YourOffersActivity, CollectionCodeActivity::class.java)
            intent.putExtra(Constants.USER_ID_KEY, it.sellerId)
            intent.putExtra(Constants.CAN_COUNT_KEY, it.canCount)
            intent.putExtra(Constants.PLASTIC_COUNT_KEY, it.plasticCount)
            intent.putExtra(Constants.GLASS_COUNT_KEY, it.glassCount)
            intent.putExtra(Constants.PAPER_COUNT_KEY, it.paperCount)
            startActivity(intent)
        }
        database = FirebaseDatabase.getInstance()
        firestore = FirebaseFirestore.getInstance()
        collectorId = FirebaseAuth.getInstance().uid
        binding.yourOffersRv.setHasFixedSize(true)
        binding.yourOffersRv.layoutManager = LinearLayoutManager(this@YourOffersActivity)
        if (!collectorId.isNullOrEmpty()) {
            database.reference.child(Constants.SELLERS_STATUS_PATH)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        offers = ArrayList()
                        for (statue in snapshot.children) {
                            if (statue.child(Constants.COLLECTOR_ID_PATH).value.toString() == collectorId) {
                                val sellerName =
                                    statue.child(Constants.SELLER_NAME_PATH).value.toString()
                                val sellerPhone =
                                    statue.child(Constants.SELLER_PHONE_PATH).value.toString()
                                val sellerId = statue.child(Constants.USER_ID_PATH).value.toString()
                                val canCount =
                                    statue.child(Constants.CAN_COUNT_KEY).value.toString()
                                        .toInt()
                                val glassCount =
                                    statue.child(Constants.GLASS_COUNT_KEY).value.toString()
                                        .toInt()
                                val plasticCount =
                                    statue.child(Constants.PLASTIC_COUNT_KEY).value.toString()
                                        .toInt()
                                val paperCount =
                                    statue.child(Constants.PAPER_COUNT_KEY).value.toString()
                                        .toInt()
                                sellerId?.let { checkedSellerId ->
                                    val offer = Offer(
                                        sellerName, sellerPhone, checkedSellerId,
                                        canCount, plasticCount, glassCount, paperCount
                                    )
                                    offers.add(offer)
                                }

                            }

                        }
                        adapter.offers = offers
                        adapter.notifyDataSetChanged()
                        binding.yourOffersRv.adapter = adapter

                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            this@YourOffersActivity,
                            error.toException().localizedMessage ?: "Unknown error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        } else {
            Toast.makeText(this@YourOffersActivity, "Can't find seller data", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun help(view: View) {
        val a =Intent(this@YourOffersActivity,HelpActivity::class.java)
        startActivity(a)

    }
    fun orderthis1(view: View) {
        val a =Intent(this@YourOffersActivity,CollectorCollections::class.java)
        startActivity(a)

    }
    fun messagecenter(view: View) {
        val a =Intent(this@YourOffersActivity,Feedback::class.java)
        startActivity(a)
    }
}