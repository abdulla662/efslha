package com.example.efslha

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MessageCenterActivity : AppCompatActivity() {
    lateinit var rere: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_center)
        val currentUser = Firebase.auth.currentUser?.uid
        rere = findViewById(R.id.rere)

        val database = FirebaseDatabase.getInstance().reference
        database.child("sellers statue")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (sellerSnapshot in dataSnapshot.children) {
                        val sellerName = sellerSnapshot.child("name").getValue(String::class.java)
                        val sellerPhone = sellerSnapshot.child("phone").getValue(String::class.java)
                        val userid =sellerSnapshot.child(Constants.USER_ID_PATH).getValue(String::class.java)
                        val collector =sellerSnapshot.child(Constants.COLLECTOR_ID_PATH).getValue(String::class.java)

                        if (currentUser == collector) {
                            val displayText =
                                "The seller name is $sellerName, seller phone is $sellerPhone, userid is $userid, and collector id is $collector"
                            rere.setText(displayText)
                        }else{
                            rere.setText("")
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle possible errors
                    println("Error loading data: ${databaseError.message}")
                }
            })


    }




        fun collections(view: View) {
            val a = Intent(this, CollectorCollections::class.java)
            startActivity(a)
        }

        fun msgcenter(view: View) {
            val a = Intent(this, MessageCenterActivity::class.java)
            startActivity(a)
        }

        fun help(view: View) {
            val a = Intent(this, HelpActivity::class.java)
            startActivity(a)
        }
    }
