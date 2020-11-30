package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


const val TOPIC = "/topics/myTopic"

class MainActivity : AppCompatActivity() {

    private var db: FirebaseFirestore? = null

    private lateinit var tmp : String

    private var databaseManager: DatabaseManager? = null


    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = FirebaseFirestore.getInstance()

        databaseManager = DatabaseManager(this)

        println(intent.getStringExtra("users"))

        if(intent != null) {

            if (intent.hasExtra("users")) {
                tmp = intent.getStringExtra("users")

                println("laaaaaaaa")
                println(tmp)


                db!!.collection("Users")
                        .document(tmp)
                        .get()
                        .addOnSuccessListener { document ->

                            var myToken = document.getString("token").toString()
                            println(myToken)
                            databaseManager!!.insertToken(myToken)
                        }

            }
        }

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        btnSend.setOnClickListener {
            val message = etMessage.text.toString()
            val sender = intent.getStringExtra("users")
            val myToken = databaseManager!!.token
            println(myToken)
            if(message.isNotEmpty() && sender.isNotEmpty()) {
                PushNotification(
                        NotificationData(message, sender),
                        myToken
                ).also {
                    sendNotification(it)
                }
            }
        }
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d(TAG, "Response: ${Gson().toJson(response)}")
            } else {
                Log.e(TAG, response.errorBody().toString())
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

}
