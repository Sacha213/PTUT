package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.iid.FirebaseInstanceId
import java.util.HashMap


class FindToken : AppCompatActivity() {

    private val TAG = "FindToken"

    private var db: FirebaseFirestore? = null

    private var databaseManager: DatabaseManager? = null

    private var recup: MutableList<String> = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_findtoken)

        db = FirebaseFirestore.getInstance()

        databaseManager = DatabaseManager(this)


        recup = databaseManager!!.pseudo


        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            FirebaseService.token = it.token
            val newToken = it.token

            val data = hashMapOf(
                    "prenom" to recup.get(0),
                    "nom" to recup.get(1),
                    "token" to newToken

            )

            db!!.collection("Users").document(recup.get(0)+" "+recup.get(1)).set(data)

            /******************* Changement de page  */
            val otherActivity = Intent(applicationContext, Information::class.java) //Ouverture d'une nouvelle activité

            startActivity(otherActivity)

            finish() //Fermeture de l'ancienne activité*/


        }
    }
}

private fun Query.set(data: HashMap<String, String>) {
    TODO("Not yet implemented")
}


