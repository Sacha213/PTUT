package com.example.myapplication

import android.app.AlertDialog
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_chat_conversation.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


const val TOPIC = "/topics/myTopic"

class ChatLecture : AppCompatActivity() {

    /******************* Attribut  *******************/
    private var db: FirebaseFirestore? = null
    private var databaseManager: DatabaseManager? = null
    private var listeMessage: MutableList<Message> = ArrayList<Message>()
    private lateinit var recyclerView: RecyclerView;
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var listAdapter: MessageAdapter? = null
    private lateinit var date: Date
    private var mAuth: FirebaseAuth? = null

    val TAG = "MainActivity"

    private lateinit var write: ImageView
    private lateinit var menu : Menu
    private lateinit var textChat : TextView

    private lateinit var NOM : String
    private lateinit var PRENOM : String
    private lateinit var DESTINATAIRE :String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_conversation)

        /******************* Initialisation des variables *******************/
        this.textChat = findViewById(R.id.textChat)

        db = FirebaseFirestore.getInstance()
        databaseManager = DatabaseManager(this)
        mAuth = FirebaseAuth.getInstance()

        this.menu = Menu(this, databaseManager)

        //On récupère le pseudo du destinataire
        DESTINATAIRE =  intent.getStringExtra("users")
        //On affiche le pseudo du destinataire
        textChat.setText(DESTINATAIRE)


        listeMessage = databaseManager!!.listMessage(intent.getStringExtra("users"))
        recyclerView = findViewById(R.id.recyclerview_message)
        val mLinearLayoutManager = LinearLayoutManager(this)
        mLinearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLinearLayoutManager)
        listAdapter = MessageAdapter(listeMessage)
        recyclerView.setAdapter(listAdapter)

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager //Gestionnaire connexion réseau
        val networkInfo = connectivityManager.activeNetworkInfo //Information du réseau


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
                        } else {
                            println("Erreur")
                        }
                    }
        }

        /******************* Mise en place d'écouteur : Envoie du message *******************/
        btnSend.setOnClickListener {

            if (networkInfo != null){
                val message = etMessage.text.toString()

                //On récupère le token du destinataire
                db!!.collection("Token")
                        .document(DESTINATAIRE)
                        .get()
                        .addOnCompleteListener { task ->
                            val document = task.result
                            if (document!!.exists()) {
                                val myToken = document.getString("token").toString()

                                //On lui envoie une notification
                                if(message.isNotEmpty() && NOM.isNotEmpty() && PRENOM.isNotEmpty() && myToken.isNotEmpty()) {
                                    date = Date()
                                    databaseManager!!.insertMessage(message, DESTINATAIRE, 2, date)
                                    PushNotification(
                                            NotificationData(message, PRENOM + " " + NOM),
                                            myToken
                                    ).also {
                                        sendNotification(it)
                                    }
                                }

                                //On actualise l'activité
                                val intent = Intent(this, ChatLecture::class.java)
                                intent.putExtra("users", DESTINATAIRE)
                                startActivity(intent)

                                finish() //Fermeture de l'ancienne activité
                                overridePendingTransition(0, 0)


                            } else {
                                println("Erreur")
                            }

                        }
            }
            else{
                //On préviens l'utilisateur
                val erreurInternet = AlertDialog.Builder(this)
                erreurInternet.setTitle("Oups...") //Titre
                erreurInternet.setMessage("Il semblerait que vous n'êtes pas connecté à internet.\nVous ne pouvez donc pas envoyer votre message.") //Message
                erreurInternet.setIcon(R.drawable.wifi) //Ajout de l'image
                erreurInternet.show() //Affichage de la boîte de dialogue

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

    /******************* Gestion du retour en arrière  *******************/
    override fun onBackPressed() {
        /******************* Changement de page  *******************/
        val otherActivity = Intent(applicationContext, ChatReception::class.java) //Ouverture d'une nouvelle activité
        startActivity(otherActivity)

        //On ferme la database
        databaseManager!!.close()

        finish() //Fermeture de l'ancienne activité
        overridePendingTransition(0, 0) //Suprimmer l'animation lors du changement d'activité
    }

}


