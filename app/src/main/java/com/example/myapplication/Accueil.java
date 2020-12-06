package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentChange.Type;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Query.Direction;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class Accueil extends AppCompatActivity {

    /******************* Attribut *******************/
    private Button continuer;
    private EditText casePseudo;
    private AlertDialog.Builder bienvenueDialogue; //Boite de dialogue pour un message de bienvenue

    private EditText mail;
    private EditText adresseTomuss;
    private EditText adresseCalendrier;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db; //Base de donnée Firestore

    //Base de données
    private DatabaseManager databaseManager;

    private String id;
    private String mdp;
    private String nom;
    private String prenom;

    private AlertDialog.Builder erreur;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        /******************* Initialisation des variables *******************/
        this.continuer = findViewById(R.id.boutonAccueil);
        bienvenueDialogue = new AlertDialog.Builder(this); //Création de la boîte de dialogue

        this.mail = findViewById(R.id.adresseMail);
        this.adresseCalendrier = findViewById(R.id.adresseCalendrier);
        this.adresseTomuss = findViewById(R.id.adresseTommus);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance(); // Acces à la base de donnée cloud firestore

        databaseManager = new DatabaseManager(this);

        //On récupère le mot de passe et l'identifiant transmit par l'ancienne activité
        Intent intent = getIntent();
        id = intent.getStringExtra("Identifiant");
        mdp = intent.getStringExtra("MotDePasse");

        erreur = new AlertDialog.Builder(this); //création de la boîte de dialogue




        /******************* Affichage de la boîte de dialogue de bienvenue *******************/ // à enlever après avoir créer un parcours d'initialisation de l'application pour l'utilisateur
        bienvenueDialogue.setTitle("Tu y est presque..."); //Titre
        bienvenueDialogue.setMessage("Nous avons encore besoin de quelques informations pour pouvoir activer toutes les fonctionnalités de l’application.\n" +
                "\n" +
                "* Il te faut maintenant renseigner ton adresse mail de l’université Lyon 1.\n" +
                "* Mais aussi le lien du flux RSS de tes notes que tu retrouveras sur Tomuss.\n" +
                "* Et enfin le lien de ton calendrier accessible en l’exportant depuis internet."); //Message
        bienvenueDialogue.setIcon(R.drawable.checklist); //Ajout de l'icone valider
        bienvenueDialogue.show(); //Affichage de la boîte de dialogue


        /******************* Mise en place d'écouteur *******************/
        continuer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {



                //On récupère les information entrées par l'utilisateur
                String adresseMail = String.valueOf(mail.getText()); //Adresse mail
                String lienTomuss = String.valueOf(adresseTomuss.getText());
                String lienCalendrier = String.valueOf(adresseCalendrier.getText());

                //On vérifie que l'utilisateur à bien remplie tout les champs
                if (adresseMail.length()>10 && lienCalendrier.length()>10 && lienTomuss.length()>10){


                /******************* Connection de l'utilisateur *******************/

                //Etape 1 : On connecte l'utilisateur
                mAuth.signInWithEmailAndPassword(String.valueOf(mail.getText()), mdp)
                        .addOnCompleteListener(Accueil.this, task -> {
                            if (task.isSuccessful()) {
                                //Etape 2 : Si l'utilisateur est connecté on change d'activité

                                //On enregistre les données de l'utilisateur dans la base de données local
                                databaseManager.insertUser(id, adresseMail, lienTomuss, lienCalendrier);

                                /******************* Changement de page *******************/

                                Intent otherActivity = new Intent(getApplicationContext(), FindToken.class); //Ouverture d'une nouvelle activité
                                startActivity(otherActivity);

                                finish();//Fermeture de l'ancienne activité
                                overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité



                            } else {
                                //Si il n'y a pas de compte, on en créer un

                                //On récupère les différentes informations du mail
                                String partie1 = adresseMail.split("@")[0]; //La partie avant @
                                prenom = partie1.split("\\.")[0]; //Avant le .
                                nom = partie1.split("\\.")[1]; //Après le .

                                //On met en majuscule la première lettre
                                prenom = prenom.substring(0,1).toUpperCase() + prenom.substring(1);
                                nom = nom.substring(0,1).toUpperCase() + nom.substring(1);

                                databaseManager.insertPseudo(prenom, nom);


                                //Etape 2 : On crée le compte utilisateur
                                mAuth.createUserWithEmailAndPassword(adresseMail, mdp)
                                        .addOnCompleteListener(Accueil.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // Etape 3 : Si la création a fonctionnée alors on enregistre le mot de passe de l'utilisateur dans une base de données sécurisé (PS: l'utilisateur est connecté)

                                                //Création data pour la BD Firebase
                                                Map<String, Object> data = new HashMap<>();
                                                data.put("Uid",mAuth.getCurrentUser().getUid());
                                                data.put("Password",mdp);
                                                data.put("Nom", nom);
                                                data.put("Prénom", prenom);

                                                db.collection("users").add(data);

                                                //On enregistre les données de l'utilisateur dans la base de données local
                                                databaseManager.insertUser(id, adresseMail, lienTomuss, lienCalendrier);

                                                //Etape 4 : On change d'activité

                                                /******************* Changement de page *******************/

                                                Intent otherActivity = new Intent(getApplicationContext(), FindToken.class); //Ouverture d'une nouvelle activité
                                                startActivity(otherActivity);

                                                finish();//Fermeture de l'ancienne activité
                                                overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité


                                            }
                                            else {
                                                //Etape 3 : Il y a un problème de création de compte ou de connection (ex : pas d'internet)

                                                /******************* Affichage de la boîte de dialogue d'erreur *******************/
                                                erreur.setTitle("Oups..."); //Titre
                                                erreur.setMessage("Un problème est survenu lors de la création de votre compte, veuillez réessayer plus tard"); //Message
                                                erreur.setIcon(R.drawable.road_closure); //Ajout de l'émoji caca
                                                erreur.show(); //Affichage de la boîte de dialogue

                                            }


                                            }
                                        });


                            }
                        });


                }

                else{
                    //On affiche une erreur

                    /******************* Affichage de la boîte de dialogue d'erreur *******************/
                    erreur.setTitle("Oups..."); //Titre
                    erreur.setMessage("Il semblerait que vous n'avez pas entré toutes les informations"); //Message
                    erreur.setIcon(R.drawable.road_closure); //Ajout de l'émoji caca
                    erreur.show(); //Affichage de la boîte de dialogue
                }

            }
        });
    }

    /******************* Gestion du retour en arrière *******************/
    @Override
    public void onBackPressed() {

        super.onBackPressed();

    }
}