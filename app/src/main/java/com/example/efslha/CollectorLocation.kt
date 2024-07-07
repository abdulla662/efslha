package com.example.efslha

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.efslha.databinding.ActivityCollectorLocationBinding
import com.example.efslha.ui.main.Feedback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore


class CollectorLocation : AppCompatActivity() {
    private lateinit var binding: ActivityCollectorLocationBinding
    private lateinit var sp: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var firestore: FirebaseFirestore
    private lateinit var database: FirebaseDatabase
    private var collectorId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollectorLocationBinding.inflate(layoutInflater)
        sp = getSharedPreferences(Constants.USER_DATA, MODE_PRIVATE)
        editor = sp.edit()
        if (sp.getBoolean(Constants.IS_LOCATION_CONFIRMED, false)) {
            val a = Intent(this, CollectorHome::class.java)
            startActivity(a)
            finish()
            return
        }
        database = FirebaseDatabase.getInstance()
        firestore = FirebaseFirestore.getInstance()
        collectorId = FirebaseAuth.getInstance().uid

        binding.apply {
            setContentView(root)
            val data = arrayListOf("Cairo")
            data.add(1, "Giza")
            data.add(2, "Alexandria")
            data.add(3, "Behira")
            var aa = ArrayAdapter(this@CollectorLocation, android.R.layout.simple_spinner_item, data)
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = aa
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    if (selectedItem == "Giza" || selectedItem == "Alexandria" || selectedItem == "Behira") {
                        Toast.makeText(this@CollectorLocation, "Coming soon", Toast.LENGTH_SHORT)
                            .show()
                        spinner.setSelection(0) // Reset selection to first item
                    }

                    confirmLocationBtn.setOnClickListener {
                        if (collectorId != null) {

                            val locationData = hashMapOf<String, Any>(
                                Constants.LOCATION_FIELD to spinner.selectedItem.toString(),
                            )

                            firestore.collection(Constants.COLLECTORS_COLLECTION)
                                .document(collectorId!!)
                                .update(locationData).addOnCompleteListener { task ->
                                    val intent =
                                        Intent(this@CollectorLocation, CollectorHome::class.java)
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            this@CollectorLocation,
                                            "Location confirmed successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        editor.putBoolean(Constants.IS_LOCATION_CONFIRMED, true)
                                        editor.apply()
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(
                                            this@CollectorLocation,
                                            task.exception?.localizedMessage ?: "Unknown error",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                        } else {
                            Toast.makeText(
                                this@CollectorLocation,
                                "Can't find collector data",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
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


