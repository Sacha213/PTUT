package com.example.myapplication

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.iid.FirebaseInstanceId


class FindToken  {

    /******************* Attribut *******************/

    private val TAG = "FindToken"

    private var db: FirebaseFirestore? = null
    private var mAuth: FirebaseAuth? = null

    private lateinit var NOM : String
    private lateinit var PRENOM : String


    fun FindToken(context: Context) {

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()


        // On récupère le prénom et le nom de l'utilisateur
        mAuth!!.currentUser?.uid?.let {
            db!!.collection("users")
                    .document(it)
                    .get()
                    .addOnCompleteListener { task ->
                        val document = task.result
                        if (document!!.exists()) {

                            NOM = document.getString("Nom").toString()
                            PRENOM = document.getString("Prénom").toString()

                            FirebaseService.sharedPref = context.getSharedPreferences("sharedPref", MODE_PRIVATE)
                            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                                FirebaseService.token = it.token
                                val newToken = it.token

                                val data = hashMapOf(
                                        "token" to newToken
                                )

                                //On ajoute le token à la base de donner
                                //(SetOptions.merge() sert à ne pas suprimmer les ancienne données)
                                //mAuth!!.currentUser?.getUid()?.let { it1 -> db!!.collection("users").document(it1).set(data, SetOptions.merge()) }

                                db!!.collection("Token")
                                        .document(PRENOM+" "+NOM)
                                        .set(data)
                            }


                        }else {
                            println("Erreur")
                        }

                    }

        }






    }

}




