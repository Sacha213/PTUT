package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId


class FindToken : AppCompatActivity() {

    private val TAG = "FindToken"

    private var db: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_findtoken)

        db = FirebaseFirestore.getInstance()

        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            FirebaseService.token = it.token
            val newToken = it.token

            val user = hashMapOf(
                    "ID" to "p1913943",
                    "Token" to newToken
            )
            db!!.collection("Users")
                    .add(user)
                    .addOnSuccessListener { documentReference ->
                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                    }

            /******************* Changement de page  */
            val otherActivity = Intent(applicationContext, Information::class.java) //Ouverture d'une nouvelle activité

            startActivity(otherActivity)

            finish() //Fermeture de l'ancienne activité*/


        }
    }
}