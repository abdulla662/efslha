package com.example.efslha.ui.main

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import com.example.efslha.Constants
import com.example.efslha.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class GoldFragment : Fragment() {
    lateinit var image1: ImageView
    lateinit var talbattiehouse: ImageView
    lateinit var db: FirebaseFirestore
    private var id: String? = null
    lateinit var points1: TextView
    lateinit var textviewtalbat: TextView
    lateinit var points250: TextView
    lateinit var handAnimationView: LottieAnimationView
    var isTalbatOfferChosen = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gold, container, false)
        image1 = view.findViewById(R.id.image1)
        talbattiehouse = view.findViewById(R.id.talbattiehouse)
        points1 = view.findViewById(R.id.points1)
        db = FirebaseFirestore.getInstance()
        id = FirebaseAuth.getInstance().uid
        textviewtalbat = view.findViewById(R.id.textviewtalbat)
        points250 = view.findViewById(R.id.points250)
        handAnimationView = view.findViewById(R.id.hand_animation_view)
        textviewtalbat.setOnLongClickListener {
            copyTextToClipboard(textviewtalbat.text.toString())
            true
        }
        changeCode()
        talbattiehouse.setOnClickListener {
            if (isTalbatOfferChosen) {
                Toast.makeText(requireContext(), "You have already chosen this offer.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val points = points1.text.toString().toInt()
            if (points >= 2000) {
                if (id != null) {
                    db.collection(Constants.SELLERS_COLLECTION)
                        .document(id!!).get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful && task.result != null) {
                                val documentSnap = task.result
                                val pointsValue = documentSnap.getLong(Constants.POINTS_FIELD)
                                if (pointsValue != null && pointsValue >= 2000) {
                                    isTalbatOfferChosen = true
                                    playHandAnimation()
                                    val newPointsValue = pointsValue - 2000
                                    db.collection(Constants.SELLERS_COLLECTION)
                                        .document(id!!)
                                        .update(Constants.POINTS_FIELD, newPointsValue)
                                        .addOnSuccessListener {
                                            points1.text = newPointsValue.toString()
                                            Toast.makeText(
                                                requireContext(),
                                                "Points deducted successfully!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(
                                                requireContext(),
                                                "Failed to update points: ${it.localizedMessage}",
                                                Toast.LENGTH_SHORT).show()
                                        }
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Not enough points",
                                        Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Error: ${task.exception?.localizedMessage ?: "Unknown error occurred"}",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(requireContext(), "Error: ID is null", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Not enough points", Toast.LENGTH_SHORT).show()
            }
        }

        if (id != null) {
            db.collection(Constants.SELLERS_COLLECTION)
                .document(id!!).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        val documentSnap = task.result
                        val pointsValue = documentSnap.getLong(Constants.POINTS_FIELD)
                        points1.text = pointsValue.toString()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Error: ${task.exception?.localizedMessage ?: "Unknown error occurred"}",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(requireContext(), "Error: ID is null", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun changeCode() {
        if (id != null) {
            val generatedCode = generateRandomCode(5)
            textviewtalbat.text = (" EFSL$generatedCode")
        }
    }

    private fun generateRandomCode(length: Int): String {
        val allowedChars = ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private fun playHandAnimation() {
        handAnimationView.visibility = View.VISIBLE
        handAnimationView.playAnimation()
        animateScratchEffect(points250, textviewtalbat)
        handAnimationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                handAnimationView.visibility = View.GONE
                textviewtalbat.visibility = View.VISIBLE
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    private fun animateScratchEffect(viewToAnimate: View, targetView: View) {
        // إعداد تحريك الشفافية
        val alphaAnimator = ValueAnimator.ofFloat(1f, 0f)
        alphaAnimator.addUpdateListener { animation ->
            val alpha = animation.animatedValue as Float
            viewToAnimate.alpha = alpha
        }

        // إعداد تحريك الانتقال
        val translationXAnimator = ObjectAnimator.ofFloat(viewToAnimate, "x", viewToAnimate.x, targetView.x)
        val translationYAnimator = ObjectAnimator.ofFloat(viewToAnimate, "y", viewToAnimate.y, targetView.y)

        // تجميع التحريكات معًا باستخدام AnimatorSet
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(alphaAnimator, translationXAnimator, translationYAnimator)
        animatorSet.duration = handAnimationView.duration

        // بدء التحريكات
        animatorSet.start()
    }
    private fun copyTextToClipboard(text: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show()
    }
}

