package com.example.efslha

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.efslha.databinding.ActivityCollectorHomeBinding
import com.example.efslha.ui.main.Feedback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class CollectorHome : AppCompatActivity() {
    lateinit var database: FirebaseDatabase
    lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    private lateinit var sp:SharedPreferences
    private fun logout (){

            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this,LoginSignupActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }

    private lateinit var binding: ActivityCollectorHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollectorHomeBinding.inflate(layoutInflater)
        sp=getSharedPreferences(Constants.USER_DATA, MODE_PRIVATE)
        binding.apply {

            setContentView(root)
            auth = FirebaseAuth.getInstance()
            collectorLogout.setOnClickListener {
                logout()
            }
            firestore = FirebaseFirestore.getInstance()
            val currentUser = auth.currentUser
            val uid = currentUser?.uid
            if (uid != null) {
                firestore.collection(Constants.COLLECTORS_COLLECTION).document(uid)
                    .get() // Use 'firestore' instead of 'firestore1'
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            val userData = documentSnapshot.data
                            var userName = userData?.get("name") as? String
                            if (userName != null) {
                                collectorHomeTitle.text =
                                    "Hello $userName" // Use 'nametext.text' instead of 'name.text'
                            }
                        } else {
                            collectorHomeTitle.text =
                                "Name is not available, try again" // Use 'nametext.text' i
                        }
                    }
            }
            openCodeBtn.setOnClickListener {
                startActivity(
                    Intent(this@CollectorHome,
                        YourOffersActivity::class.java
                    ).putExtra(Constants.COLLECTOR_ID_PATH, uid)
                )
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

    fun Glass(view: View) {
        val a = Intent(this, OfferActivity::class.java)
        a.putExtra(Constants.OFFER_TYPE,Constants.GLASS_OFFER_TYPE)
        startActivity(a)
    }

    fun Paper(view: View) {
        val a = Intent(this, OfferActivity::class.java)
        a.putExtra(Constants.OFFER_TYPE,Constants.PAPER_OFFER_TYPE)
        startActivity(a)
    }

    fun Can(view: View) {
        val a = Intent(this, OfferActivity::class.java)
        a.putExtra(Constants.OFFER_TYPE,Constants.CAN_OFFER_TYPE)
        startActivity(a)
    }

    fun Plastic(view: View) {
        val a = Intent(this, OfferActivity::class.java)
        a.putExtra(Constants.OFFER_TYPE,Constants.PLASTIC_OFFER_TYPE)
        startActivity(a)
    }

    fun collectorpackage(view: View) {
        val a = Intent(this, OfferActivity::class.java)
        a.putExtra(Constants.OFFER_TYPE,Constants.PACKAGE_OFFER_TYPE)
        startActivity(a)
    }
}