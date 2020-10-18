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

public class Accueil extends AppCompatActivity {

    /******************* Attribut *******************/
    private Button continuer;
    private AlertDialog.Builder bienvenueDialogue; //Boite de dialogue pour un message de bienvenue
    private EditText mail;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db; //Base de donnée Firestore

    //Base de données
    private DatabaseManager databaseManager;

    private String id;
    private String mdp;
    private String nom;
    private String prenom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        /******************* Initialisation des variables *******************/
        this.continuer = findViewById(R.id.boutonAccueil);
        bienvenueDialogue = new AlertDialog.Builder(this); //Création de la boîte de dialogue
        this.mail = findViewById(R.id.adresseMail);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance(); // Acces à la base de donnée cloud firestore

        databaseManager = new DatabaseManager(this);

        //On récupère le mot de passe et l'identifiant transmit par l'ancienne activité
        Intent intent = getIntent();
        id = intent.getStringExtra("Identifiant");
        mdp = intent.getStringExtra("MotDePasse");



        /******************* Affichage de la boîte de dialogue de bienvenue *******************/ // à enlever après avoir créer un parcours d'initialisation de l'application pour l'utilisateur
        bienvenueDialogue.setTitle("Bienvenue"); //Titre
        bienvenueDialogue.setMessage("Bravo, tu as réussi à te connecter "); //Message
        bienvenueDialogue.setIcon(R.drawable.valider); //Ajout de l'icone valider
        bienvenueDialogue.show(); //Affichage de la boîte de dialogue



        /******************* Mise en place d'écouteur *******************/
        continuer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                /******************* Connection de l'utilisateur *******************/

                //Etape 1 : On connecte l'utilisateur
                mAuth.signInWithEmailAndPassword(String.valueOf(mail.getText()), mdp)
                        .addOnCompleteListener(Accueil.this, task -> {
                            if (task.isSuccessful()) {
                                //Etape 2 : Si l'utilisateur est connecté on change d'activité

                                //On enregistre l'id lyon1 dans la base de données local
                                databaseManager.insertIdentifiant(id);

                                /******************* Changement de page *******************/

                                Intent otherActivity = new Intent(getApplicationContext(), Information.class); //Ouverture d'une nouvelle activité
                                startActivity(otherActivity);

                                finish();//Fermeture de l'ancienne activité
                                overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité



                            } else {
                                //Si il n'y a pas de compte, on en créer un

                                //On récupère les différentes informations du mail
                                String adresseMail = String.valueOf(mail.getText()); //Adresse mail
                                String partie1 = adresseMail.split("@")[0]; //La partie avant @
                                prenom = partie1.split("\\.")[0]; //Avant le .
                                nom = partie1.split("\\.")[1]; //Après le .

                                //On met en majuscule la première lettre
                                prenom = prenom.substring(0,1).toUpperCase() + prenom.substring(1);
                                nom = nom.substring(0,1).toUpperCase() + nom.substring(1);


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

                                                //On enregistre l'id lyon1 dans la base de données local
                                                databaseManager.insertIdentifiant(id);

                                                //Etape 4 : On change d'activité

                                                /******************* Changement de page *******************/

                                                Intent otherActivity = new Intent(getApplicationContext(), Information.class); //Ouverture d'une nouvelle activité
                                                startActivity(otherActivity);

                                                finish();//Fermeture de l'ancienne activité
                                                overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité


                                            } else {
                                                //Etape 3 : Il y a un problème de création de compte ou de connection (ex : pas d'internet)

                                                // On affiche un message d'erreur
                                                System.out.println("We have a problem");

                                            }

                                            // ...
                                            }
                                        });


                            }
                        });



            }
        });
    }

    /******************* Gestion du retour en arrière *******************/
    @Override
    public void onBackPressed() {

        super.onBackPressed();

    }
}