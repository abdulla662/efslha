package com.example.efslha

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.efslha.databinding.InfoDialogBinding
import com.example.efslha.databinding.OfferItemBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class OffersAdapter(private val activity: Activity, var offers: List<Offer>,val listener: (Offer) -> Unit) :
    RecyclerView.Adapter<OffersAdapter.OfferViewHolder>() {
    inner class OfferViewHolder(private val offerItemBinding: OfferItemBinding)
        : ViewHolder(offerItemBinding.root) {
        fun fillOfferData(offer: Offer) {
            offerItemBinding.apply {
                offerItemCard.setOnClickListener {
                    listener(offer)
                }
                viewOfferBtn.setOnClickListener {
                    showInfoDialog(activity,offer)
                }
                sellerNameTv.text = offer.sellerName
                sellerPhoneTv.text = offer.sellerPhone
                sellerFirstLetter.text = (offer.sellerName.toCharArray()[0]).toString().uppercase()
            }
        }

    }
    fun showInfoDialog(activity: Activity, offer: Offer) {
        var alertDialog: AlertDialog? = null
        val viewBinding = InfoDialogBinding.inflate(activity.layoutInflater)
        val builder = MaterialAlertDialogBuilder(activity)
        viewBinding.apply {
            var data = "${offer.sellerName} accepted your offer to buy amount of"
            if (offer.canCount>0)
                data += (" can quantity : ${offer.canCount},")
            if (offer.plasticCount>0)
                data += (" plastic quantity : ${offer.plasticCount},")
            if (offer.glassCount>0)
                data += (" glass quantity : ${offer.glassCount},")
            if (offer.paperCount>0)
                data += (" paper quantity : ${offer.paperCount},")
            data = if (data.get(data.lastIndex) == ',') data.substring(0,data.lastIndex) else data
            sellerInfoTv.text = data
            sellerNameTv.text = offer.sellerName
            builder.setBackground(ContextCompat.getDrawable(activity, R.drawable.dialog_bg))
            cancelBtn.setOnClickListener {
                alertDialog?.dismiss()
            }
            builder.setView(getRoot())

        }

        alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OffersAdapter.OfferViewHolder {
        return OfferViewHolder(
            OfferItemBinding.inflate(LayoutInflater.from(activity.applicationContext), parent, false)
        )
    }


    override fun onBindViewHolder(holder: OffersAdapter.OfferViewHolder, position: Int) {
        holder.fillOfferData(offers[position])
    }

    override fun getItemCount() = offers.size

}