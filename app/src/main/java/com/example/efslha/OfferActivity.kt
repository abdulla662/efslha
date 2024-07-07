package com.example.efslha

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.allViews

import com.example.efslha.databinding.ActivityOfferBinding
import com.example.efslha.databinding.SellerRequestItemBinding

import com.example.efslha.ui.main.Feedback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class OfferActivity : AppCompatActivity() {
    private var userId: String? = null
    private lateinit var binding: ActivityOfferBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var database: FirebaseDatabase
    private lateinit var offerType: String
    private var collectorId: String? = null
    private var canQuantity = 0
    private var paperQuantity = 0
    private var plasticQuantity = 0
    private var glassQuantity = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOfferBinding.inflate(layoutInflater)
        firestore = FirebaseFirestore.getInstance()
        database = FirebaseDatabase.getInstance()
        collectorId = FirebaseAuth.getInstance().uid
        offerType = intent.getStringExtra(Constants.OFFER_TYPE) ?: Constants.PACKAGE_OFFER_TYPE

        binding.apply {
            setContentView(root)
            setUpWidgets()
            getOffersAccordingToType(this@OfferActivity.offerType)
            offerBtn.setOnClickListener {
                if (userId != null && collectorId != null) {
                    val data =
                        if (this@OfferActivity.offerType != Constants.PACKAGE_OFFER_TYPE) {
                            hashMapOf(
                                Constants.USER_ID_PATH to userId!!,
                                Constants.COLLECTOR_ID_PATH to collectorId!!,
                                when (this@OfferActivity.offerType) {
                                    Constants.CAN_OFFER_TYPE -> Constants.CAN_COUNT_KEY to canQuantity
                                    Constants.PLASTIC_OFFER_TYPE -> Constants.PLASTIC_COUNT_KEY to plasticQuantity
                                    Constants.GLASS_OFFER_TYPE -> Constants.GLASS_COUNT_KEY to glassQuantity
                                    else -> Constants.PAPER_COUNT_KEY to paperQuantity
                                }
                            )
                        } else {
                            hashMapOf<String, Any>(
                                Constants.USER_ID_PATH to userId!!,
                                Constants.COLLECTOR_ID_PATH to collectorId!!,
                                Constants.CAN_COUNT_KEY to canQuantity,
                                Constants.PLASTIC_COUNT_KEY to plasticQuantity,
                                Constants.GLASS_COUNT_KEY to glassQuantity,
                                Constants.PAPER_COUNT_KEY to paperQuantity
                            )
                        }
                    val childKey =
                        when (this@OfferActivity.offerType) {
                            Constants.CAN_OFFER_TYPE -> Constants.OFFERS_CAN_CHILD
                            Constants.PAPER_OFFER_TYPE -> Constants.OFFERS_PAPER_CHILD
                            Constants.GLASS_OFFER_TYPE -> Constants.OFFERS_GLASS_CHILD
                            Constants.PLASTIC_OFFER_TYPE -> Constants.OFFERS_PLASTIC_CHILD
                            else -> Constants.OFFERS_PACKAGE_CHILD
                        }
                    database.reference.child(childKey)
                        .get().addOnCompleteListener { checkOfferTask ->
                            if (checkOfferTask.isSuccessful && checkOfferTask.result != null) {
                                var existedOffer = false
                                for (offerChild in checkOfferTask.result.children) {
                                    val offerSellerId =
                                        offerChild.child(Constants.USER_ID_PATH).value.toString()
                                    val offerCollectorId =
                                        offerChild.child(Constants.COLLECTOR_ID_PATH).value.toString()
                                    if (offerSellerId == userId && offerCollectorId == collectorId) {
                                        existedOffer = true
                                        break
                                    }
                                }
                                if (existedOffer) {
                                    Toast.makeText(
                                        this@OfferActivity,
                                        "You can't offer this seller again",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    database.reference.child(
                                        childKey
                                    ).child(userId!!).setValue(data)
                                        .addOnCompleteListener { addOfferTask ->
                                            Toast.makeText(
                                                this@OfferActivity,
                                                if (addOfferTask.isSuccessful)
                                                    "you have offered this request successfully"
                                                else addOfferTask.exception?.localizedMessage
                                                    ?: "Unknown error occurred",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            finish()
                                        }
                                }

                            } else {
                                Toast.makeText(
                                    this@OfferActivity,
                                    checkOfferTask.exception?.localizedMessage
                                        ?: "Unknown error occurred",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                } else Toast.makeText(
                    this@OfferActivity,
                    "Can't find user data",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getOffersAccordingToType(offerType: String) {
        if (offerType == Constants.PACKAGE_OFFER_TYPE) {
            database.reference.child(Constants.SELLERS_PACKAGE).child(Constants.SELLERS_CHILD)
                .get().addOnCompleteListener {
                    if (it.isSuccessful && it.result != null) {
                        addOfferToUI(it.result.children, offerType)
                    } else {
                        messageError(it.exception)
                    }
                }
        } else {
            database.reference.child(Constants.SELLERS_SEPARATE).child(Constants.SELLERS_INDIVIDUAL)
                .get().addOnCompleteListener {
                    if (it.isSuccessful && it.result != null) {
                        addOfferToUI(it.result.children, offerType)
                    } else {
                        messageError(it.exception)
                    }
                }
        }
    }

    private fun addOfferToUI(children: Iterable<DataSnapshot>, requiredOfferType: String) {
        for (sellerRequest in children) {
            val sellerReqItemBinding = SellerRequestItemBinding.inflate(layoutInflater)
            sellerReqItemBinding.apply {
                root.tag = sellerRequest.key
                sellerReqItem.tag = sellerRequest.key
                sellerReqDataTv.tag = sellerRequest.key
                val canCount =
                    sellerRequest.child(Constants.CAN_COUNT_KEY).value?.toString()?.toInt() ?: 0
                val plasticCount =
                    sellerRequest.child(Constants.PLASTIC_COUNT_KEY).value?.toString()?.toInt()
                        ?: 0
                val glassCount =
                    sellerRequest.child(Constants.GLASS_COUNT_KEY).value?.toString()?.toInt() ?: 0
                val paperCount =
                    sellerRequest.child(Constants.PAPER_COUNT_KEY).value?.toString()?.toInt() ?: 0
                val offerType =
                    sellerRequest.child(Constants.OFFER_TYPE).value ?: Constants.PACKAGE_OFFER_TYPE
                if (offerType == requiredOfferType) {

                    var data = "${
                        sellerRequest.child(Constants.SELLER_NAME_PATH).value.toString()
                    } requests amount of"

                    if (canCount > 0) data += " cans: $canCount,"
                    if (plasticCount > 0) data += " plastic: $plasticCount,"
                    if (glassCount > 0) data += " glass: $glassCount,"
                    if (paperCount > 0) data += " paper: $paperCount,"
                    sellerReqDataTv.text = if (data.get(data.lastIndex) == ',') data.substring(
                        0,
                        data.length - 1
                    ) else data
                    sellerReqItem.setOnClickListener {
                        if (sellerReqDataTv.background.constantState == ContextCompat.getDrawable(
                                this@OfferActivity, R.drawable.plaintextfigure
                            )?.constantState
                        ) {
                            userId = sellerRequest.key.toString()
                            canQuantity = canCount
                            plasticQuantity = plasticCount
                            glassQuantity = glassCount
                            paperQuantity = paperCount
                            binding.offerBtn.visibility = VISIBLE

                            // TODO change the the background of all the sellerReqItem to plaintextfigure
                            findConstraintLayouts(binding.offerItemsLayout).forEach {
                                if (it.tag != sellerRequest.key)
                                    it.setBackgroundResource(R.drawable.plaintextfigure)
                            }
                            sellerReqItem.setBackgroundResource(R.drawable.new_background)

                        } else {
                            sellerReqItem.setBackgroundResource(R.drawable.plaintextfigure)
                            userId = null
                            canQuantity = 0
                            plasticQuantity = 0
                            glassQuantity = 0
                            paperQuantity = 0
                            binding.offerBtn.visibility = GONE

                        }
                    }
                    binding.offerItemsLayout.addView(root)
                }
            }
        }
    }

    fun findConstraintLayouts(view: View): List<TextView> {
        val constraintLayouts = mutableListOf<TextView>()
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)
                if (child is TextView) {
                    constraintLayouts.add(child)
                } else {
                    constraintLayouts.addAll(findConstraintLayouts(child))
                }
            }
        }
        return constraintLayouts
    }

    private fun messageError(exception: Exception?) {
        Toast.makeText(
            this@OfferActivity,
            exception?.localizedMessage ?: "Unknown error",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun setUpWidgets() {
        binding.apply {
            offerIcon.setImageResource(
                when (this@OfferActivity.offerType) {
                    Constants.CAN_OFFER_TYPE -> R.drawable.can
                    Constants.PLASTIC_OFFER_TYPE -> R.drawable.plastic
                    Constants.GLASS_OFFER_TYPE -> R.drawable.glass
                    Constants.PAPER_OFFER_TYPE -> R.drawable.paper
                    else -> R.drawable.packagerec
                }
            )
            if (this@OfferActivity.offerType == Constants.PACKAGE_OFFER_TYPE)
                offerIcon.imageTintList = null
            try {
                val capitalizedOfferType =
                    this@OfferActivity.offerType.uppercase()[0] + this@OfferActivity.offerType.substring(
                        1
                    )
                offerType.text = capitalizedOfferType
            } catch (e: Exception) {
                offerType.text = this@OfferActivity.offerType
            }

        }
    }

    fun help(view: View) {
        val a = Intent(this@OfferActivity, HelpActivity::class.java)
        startActivity(a)
    }
    fun collections(view: View) {
        val a=Intent(this@OfferActivity,CollectorCollections::class.java)
        startActivity(a)
    }
    fun msgcenter(view: View) {
        val a=Intent(this@OfferActivity,Feedback::class.java)
        startActivity(a)
    }
}