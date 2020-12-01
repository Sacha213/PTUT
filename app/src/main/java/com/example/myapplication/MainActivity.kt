package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


const val TOPIC = "/topics/myTopic"

class MainActivity : AppCompatActivity() {

    private var db: FirebaseFirestore? = null
    private lateinit var tmp : String
    private var databaseManager: DatabaseManager? = null
    private var listeMessage: MutableList<Message> = ArrayList<Message>()
    private lateinit var recyclerView: RecyclerView;
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var listAdapter: MessageAdapter? = null
    private lateinit var date: Date

    val TAG = "MainActivity"

    /******************* Attribut  */
    private lateinit var calendrier: ImageView
    private lateinit var notes: ImageView
    private lateinit var informations: ImageView
    private lateinit var drive: ImageView
    private lateinit var messagerie: ImageView
    private lateinit var write: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = FirebaseFirestore.getInstance()

        databaseManager = DatabaseManager(this)

        if(intent != null) {
            if (intent.hasExtra("users")) {
                tmp = intent.getStringExtra("users")

                db!!.collection("Users")
                        .document(tmp)
                        .get()
                        .addOnSuccessListener { document ->

                            println(tmp)
                            var myToken = document.getString("token").toString()
                            println(myToken)
                            databaseManager!!.insertToken(myToken)
                        }
            }
        }

        listeMessage = databaseManager!!.listMessage(intent.getStringExtra("users"))
        recyclerView = findViewById(R.id.recyclerview_message)
        val mLinearLayoutManager = LinearLayoutManager(this)
        mLinearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLinearLayoutManager)
        listAdapter = MessageAdapter(listeMessage)
        recyclerView.setAdapter(listAdapter)

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        btnSend.setOnClickListener {
            val message = etMessage.text.toString()
            val sender = databaseManager!!.pseudo
            val myToken = databaseManager!!.token
            println(myToken)
            if(message.isNotEmpty() && sender.isNotEmpty() && myToken.isNotEmpty()) {
                date = Date()
                databaseManager!!.insertMessage(message, intent.getStringExtra("users"), 2, date)
                PushNotification(
                        NotificationData(message, sender),
                        myToken
                ).also {
                    sendNotification(it)
                }
            }
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("users", tmp)
            startActivity(intent)
            finish() //Fermeture de l'ancienne activité
            overridePendingTransition(0, 0)
        }

        /******************* Initialisation des variables *******************/
        /******************* Initialisation des variables  */
        this.calendrier = findViewById(R.id.calendrier)
        this.notes = findViewById(R.id.notes)
        this.informations = findViewById(R.id.informations)
        this.drive = findViewById(R.id.drive)
        this.messagerie = findViewById(R.id.messagerie)



        /******************* Gestion des évènements du menu *******************/

        /******************* Gestion des évènements du menu  */

        calendrier.setOnClickListener(View.OnClickListener {
            /******************* Changement de page  */
            /******************* Changement de page  */
            val otherActivity = Intent(applicationContext, Calendrier::class.java) //Ouverture d'une nouvelle activité
            startActivity(otherActivity)
            finish() //Fermeture de l'ancienne activité
            overridePendingTransition(0, 0) //Suprimmer l'animation lors du changement d'activité
        })

        notes.setOnClickListener(View.OnClickListener {
            /******************* Changement de page  */
            /******************* Changement de page  */
            val otherActivity = Intent(applicationContext, Note::class.java) //Ouverture d'une nouvelle activité
            startActivity(otherActivity)
            finish() //Fermeture de l'ancienne activité
            overridePendingTransition(0, 0) //Suprimmer l'animation lors du changement d'activité
        })

        informations.setOnClickListener(View.OnClickListener {
            /******************* Changement de page  */
            /******************* Changement de page  */
            val otherActivity = Intent(applicationContext, Information::class.java) //Ouverture d'une nouvelle activité
            startActivity(otherActivity)
            finish() //Fermeture de l'ancienne activité
            overridePendingTransition(0, 0) //Suprimmer l'animation lors du changement d'activité
        })

        drive.setOnClickListener(View.OnClickListener {
            /******************* Changement de page  */
            /******************* Changement de page  */
            val otherActivity = Intent(applicationContext, Drive::class.java) //Ouverture d'une nouvelle activité
            startActivity(otherActivity)
            finish() //Fermeture de l'ancienne activité
            overridePendingTransition(0, 0) //Suprimmer l'animation lors du changement d'activité
        })

        messagerie.setOnClickListener(View.OnClickListener {
            /******************* Changement de page  */
            /******************* Changement de page  */
            val otherActivity = Intent(applicationContext, Messagerie::class.java) //Ouverture d'une nouvelle activité
            startActivity(otherActivity)
            finish() //Fermeture de l'ancienne activité
            overridePendingTransition(0, 0) //Suprimmer l'animation lors du changement d'activité
        })


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


