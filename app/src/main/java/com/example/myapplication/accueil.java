package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class accueil extends AppCompatActivity {

    /******************* Attribut *******************/
    private AlertDialog.Builder bienvenueDialogue; //Boite de dialogue pour un message de bienvenu
    private DatabaseManager databaseManager;//Base de données
    private BottomNavigationView bottomNav;//Menu de navigation


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        /******************* Initialisation des variables *******************/
        bienvenueDialogue = new AlertDialog.Builder(this); //Création de la boîte de dialogue
        databaseManager = new DatabaseManager(this);
        bottomNav = findViewById(R.id.activity_main_bottom_navigation);


        /******************* Affichage de la boîte de dialogue de bienvenue *******************/
        bienvenueDialogue.setTitle("Bienvenue"); //Titre
        bienvenueDialogue.setMessage("Bravo, tu as réussi à te connecter "); //Message
        bienvenueDialogue.setIcon(R.drawable.valider); //Ajout de l'icone valider
        bienvenueDialogue.show(); //Affichage de la boîte de dialogue

        System.out.println(databaseManager.getIdentifiant());//Affichage des identifiants enregistré dans la base de données (provisoire)

        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Calendrier()).commit(); //Affichage de base
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            /******************* Changement de page *******************/

            Fragment selectedFragment = null;
            switch (item.getItemId()){
                case R.id.action_calendrier:
                    selectedFragment = new Calendrier();
                    System.out.println("wsh");
                    break;

                case R.id.action_notes:
                    selectedFragment = new Notes();
                    break;

                case R.id.action_actu:
                    selectedFragment = new Actu();
                    break;

                case R.id.action_claroline:
                    selectedFragment = new Drive();
                    break;

                case R.id.action_messagerie:
                    selectedFragment = new Messagerie();
                    break;

            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

            return true;
        }
    };
}
