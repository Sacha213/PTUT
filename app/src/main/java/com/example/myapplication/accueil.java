package com.example.myapplication;


import android.app.ActionBar;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;








public class accueil extends AppCompatActivity {

    /******************* Attribut *******************/
    private AlertDialog.Builder bienvenueDialogue; //Boite de dialogue pour un message de bienvenu
    private DatabaseManager databaseManager;//Base de données
    private ScrollView layout;// afficheur scroll


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        /******************* Initialisation des variables *******************/
        bienvenueDialogue = new AlertDialog.Builder(this); //Création de la boîte de dialogue
        databaseManager = new DatabaseManager(this);
        this.layout = findViewById(R.id.scrollActu); // liaison avec le layout


        /******************* Affichage de la boîte de dialogue de bienvenue *******************/
        bienvenueDialogue.setTitle("Bienvenue"); //Titre
        bienvenueDialogue.setMessage("Bravo, tu as réussi à te connecter "); //Message
        bienvenueDialogue.setIcon(R.drawable.valider); //Ajout de l'icone valider
        bienvenueDialogue.show(); //Affichage de la boîte de dialogue

        System.out.println(databaseManager.getIdentifiant());//Affichage des identifiants enregistré dans la base de données (provisoire)

        /******************* Test *******************/

        ImageView image = new ImageView(this);
        ViewGroup.LayoutParams params = new ActionBar.LayoutParams(100,100);
        image.setLayoutParams(params);
        image.setBackgroundResource(R.drawable.article);
        layout.addView(image);





        }




}
