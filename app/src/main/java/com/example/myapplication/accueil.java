package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;

public class accueil extends AppCompatActivity {

    /******************* Attribut *******************/
    private AlertDialog.Builder bienvenueDialogue; //Boite de dialogue pour un message de bienvenu
    private DatabaseManager databaseManager;//Base de données



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        /******************* Initialisation des variables *******************/
        bienvenueDialogue = new AlertDialog.Builder(this); //Création de la boîte de dialogue
        databaseManager = new DatabaseManager(this);


        /******************* Affichage de la boîte de dialogue de bienvenue *******************/
        bienvenueDialogue.setTitle("Bienvenue"); //Titre
        bienvenueDialogue.setMessage("Bravo, tu as réussi à te connecter "); //Message
        bienvenueDialogue.setIcon(R.drawable.valider); //Ajout de l'icone valider
        bienvenueDialogue.show(); //Affichage de la boîte de dialogue

        System.out.println(databaseManager.getIdentifiant());//Affichage des identifiants enregistré dans la base de données (provisoire)

    }
}
