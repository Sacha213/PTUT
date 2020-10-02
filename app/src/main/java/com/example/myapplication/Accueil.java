package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Accueil extends AppCompatActivity {

    /******************* Attribut *******************/
    private Button continuer;
    private AlertDialog.Builder bienvenueDialogue; //Boite de dialogue pour un message de bienvenue

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        /******************* Initialisation des variables *******************/
        this.continuer = findViewById(R.id.boutonAccueil);
        bienvenueDialogue = new AlertDialog.Builder(this); //Création de la boîte de dialogue



        /******************* Affichage de la boîte de dialogue de bienvenue *******************/ // à enlever après avoir créer un parcours d'initialisation de l'application pour l'utilisateur
        bienvenueDialogue.setTitle("Bienvenue"); //Titre
        bienvenueDialogue.setMessage("Bravo, tu as réussi à te connecter "); //Message
        bienvenueDialogue.setIcon(R.drawable.valider); //Ajout de l'icone valider
        bienvenueDialogue.show(); //Affichage de la boîte de dialogue

        /******************* Mise en place d'écouteur *******************/
        continuer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /******************* Changement de page *******************/
                Intent otherActivity = new Intent(getApplicationContext(), FindToken.class); //Ouverture d'une nouvelle activité
                startActivity(otherActivity);

                finish();//Fermeture de l'ancienne activité
            }
        });
    }
}