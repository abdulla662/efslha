package com.example.efslha

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.efslha.databinding.ActivityLoginsignupBinding
import com.google.firebase.auth.FirebaseAuth

class LoginSignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginsignupBinding
    private lateinit var sp: SharedPreferences
    private var userId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginsignupBinding.inflate(layoutInflater)
        sp = getSharedPreferences(Constants.USER_DATA, MODE_PRIVATE)

        userId = FirebaseAuth.getInstance().uid
        if (userId != null) {
            val intent =
                Intent(
                    this@LoginSignupActivity,
                    if (sp.getBoolean(Constants.IS_COLLECTOR,false))
                        CollectorLocation::class.java
                    else userpage::class.java
                )
            startActivity(intent)
            finish()
        }
        binding.apply {
            setContentView(root)
        }
    }

    fun sellersignup(view: View) {
        val a =Intent(this,SellerSignup::class.java)
        startActivity(a)
    }
    fun collectorsignup(view: View) {
        val a =Intent(this,CollectorSignup::class.java)
        startActivity(a)
    }



    fun login(view: View) {
        val a =Intent(this,LoginActivity::class.java)
        startActivity(a)
    }
}