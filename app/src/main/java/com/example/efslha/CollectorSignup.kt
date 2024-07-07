package com.example.efslha

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

import com.example.efslha.databinding.ActivityCollectorsignupBinding

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class CollectorSignup : AppCompatActivity() {
    companion object {

        const val REQUEST_CHECK_SETTINGS = 5
    }

    private var locationManager: LocationManager? = null

    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var login111: Button
    private lateinit var name111: EditText
    private lateinit var phone111: EditText
    private lateinit var email111: EditText
    private lateinit var pass111: EditText
    private lateinit var retype111: EditText
    private lateinit var locationEditText: TextView
    private lateinit var adress: String
    private var lat: Double = 0.0
    private var long: Double = 0.0
    private var isPasswordVisible: Boolean = false // initialize to false
    private var isPasswordVisible1: Boolean = false
    private lateinit var binding: ActivityCollectorsignupBinding

    private var requestPermission: ActivityResultLauncher<String> =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result -> if (result) getLocationLink() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collectorsignup)
        binding = ActivityCollectorsignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        login111 = findViewById(R.id.login111)
        name111 = findViewById(R.id.name111)
        phone111 = findViewById(R.id.phone111)
        email111 = findViewById(R.id.email111)
        locationEditText = findViewById(R.id.locationEditText)
        pass111 = findViewById(R.id.pass111)
        retype111 = findViewById(R.id.retype111)
        isPasswordVisible = false
        isPasswordVisible1 = false

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationEditText.setOnClickListener { getLocationLink() }
        login111.setOnClickListener {
            val email = email111.text.toString()
            val name = name111.text.toString()
            val phone = phone111.text.toString()
            val pass = pass111.text.toString()
            val passRetype = retype111.text.toString()
            if (email.isNotEmpty() && pass.isNotEmpty() && name.isNotEmpty() && phone.isNotEmpty() && passRetype.isNotEmpty()) {
                if (pass == passRetype) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = firebaseAuth.currentUser
                                val userData = hashMapOf(
                                    "name" to name,
                                    "phone" to phone,
                                    "collector" to true,
                                    "lat" to lat,
                                    "long" to long,
                                )
                                user?.uid?.let { userId ->
                                    firestore.collection(Constants.COLLECTORS_COLLECTION).document(userId)
                                        .set(userData)
                                        .addOnSuccessListener {
                                            val intent = Intent(this, LoginActivity::class.java)
                                            startActivity(intent)
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(
                                                this,
                                                "Error adding user data to Firestore: ${e.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                            } else {
                                Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show()
            }

        }
        binding.eyeToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                pass111.inputType = InputType.TYPE_CLASS_TEXT
                binding.eyeToggle.setImageResource(R.drawable.eyeopen)
            } else {
                pass111.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.eyeToggle.setImageResource(R.drawable.eyeclosed)
            }
            pass111.setSelection(pass111.text.length)
        }

        binding.eyeToggle1.setOnClickListener {
            isPasswordVisible1 = !isPasswordVisible1
            if (isPasswordVisible1) {
                retype111.inputType = InputType.TYPE_CLASS_TEXT
                binding.eyeToggle1.setImageResource(R.drawable.eyeopen)
            } else {
                retype111.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.eyeToggle1.setImageResource(R.drawable.eyeclosed)
            }
            retype111.setSelection(retype111.text.length)
        }
    }




    private fun getLocationLink() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) requestPermission.launch(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            else if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) requestPermission.launch(
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            return
        }
        fun getLocationName(latitude: Double, longitude: Double, context: Context): String? {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null) {
                return addresses.firstOrNull()?.getAddressLine(0)
            }
            return TODO("Provide the return value")
        }


        val gpsEnabled: Boolean = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)?:false
        val networkEnabled: Boolean? =
            locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (gpsEnabled) {
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            val locationListener: LocationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    // Location updates are received here
                    val url =
                        "http://www.google.com/maps/place/" + location.latitude + "," + location.longitude
                    val locationName = getLocationName(location.latitude, location.longitude, this@CollectorSignup)
                    locationEditText.setText(locationName)
                    locationManager.removeUpdates(this)
                }

                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
                }

                override fun onProviderEnabled(provider: String) {
                }

                override fun onProviderDisabled(provider: String) {
                }
            }

            // Request location updates from both GPS and Network providers
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0f,
                locationListener
            )
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0,
                0f,
                locationListener
            )
        } else {
            val locationRequest: com.google.android.gms.location.LocationRequest = com.google.android.gms.location.LocationRequest.create()
            locationRequest.setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY)
            val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(locationRequest)
            builder.setAlwaysShow(true)

            val client: SettingsClient = LocationServices.getSettingsClient(this)

            val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

            task.addOnCompleteListener { task1: Task<LocationSettingsResponse?> ->
                try {
                    val response: LocationSettingsResponse? =
                        task1.getResult(ApiException::class.java)
                    getLocationLink()
                } catch (exception: ApiException) {
                    when (exception.statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                            val resolvable = exception as ResolvableApiException
                            resolvable.startResolutionForResult(
                                this@CollectorSignup,
                                REQUEST_CHECK_SETTINGS
                            )
                        } catch (e: SendIntentException) {
                            // Ignore the error
                        } catch (e: ClassCastException) {
                            // Ignore the error
                        }

                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {}
                    }
                }
            }
        }
    }
}

