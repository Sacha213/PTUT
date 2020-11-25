package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Calendrier extends AppCompatActivity {

    /******************* Attribut *******************/
    private ImageView calendrier; //Icônes du menu
    private ImageView notes;
    private ImageView informations;
    private ImageView drive;
    private ImageView messagerie;

    private Button deconnexion;

    //Base de données
    private DatabaseManager databaseManager;

    private FirebaseAuth mAuth;

    private String url = "https://adelb.univ-lyon1.fr/jsp/custom/modules/plannings/anonymous_cal.jsp?resources=40699&projectId=2&calType=ical&firstDate=2020-11-23&lastDate=2020-11-28";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendrier);

        /******************* Initialisation des variables *******************/
        this.calendrier = findViewById(R.id.calendrier);
        this.notes = findViewById(R.id.notes);
        this.informations = findViewById(R.id.informations);
        this.drive = findViewById(R.id.drive);
        this.messagerie = findViewById(R.id.messagerie);

        this.deconnexion = findViewById(R.id.boutonDeconnexion);

        databaseManager = new DatabaseManager(this);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE); //Gestionnaire connexion réseau
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo(); //Information du réseau


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        if (networkInfo.isAvailable()) { //On vérifie qu'il y a une connexion à internet

            TelechargementCalendrier cal = new TelechargementCalendrier();
            cal.execute();
        } else {
            affichageCalendrier();//On affiche le calendrier de la bd sans les actualisé
        }


        /******************* Mise en place d'écouteur *******************/
        deconnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /******************* Changement de page *******************/
                databaseManager.deleteAllUsers(); // Supression des données de la table USERS

                mAuth.signOut();//On se déconnecte

                finish();//Fermeture de l'ancienne activité
            }
        });

        /******************* Gestion des évènements du menu *******************/

        calendrier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /******************* Changement de page *******************/
                Intent otherActivity = new Intent(getApplicationContext(), Calendrier.class); //Ouverture d'une nouvelle activité
                startActivity(otherActivity);

                finish();//Fermeture de l'ancienne activité
                overridePendingTransition(0, 0);//Suprimmer l'animation lors du changement d'activité


            }
        });

        notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /******************* Changement de page *******************/
                Intent otherActivity = new Intent(getApplicationContext(), Note.class); //Ouverture d'une nouvelle activité
                startActivity(otherActivity);

                finish();//Fermeture de l'ancienne activité
                overridePendingTransition(0, 0);//Suprimmer l'animation lors du changement d'activité

            }
        });

        informations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /******************* Changement de page *******************/
                Intent otherActivity = new Intent(getApplicationContext(), Information.class); //Ouverture d'une nouvelle activité
                startActivity(otherActivity);

                finish();//Fermeture de l'ancienne activité
                overridePendingTransition(0, 0);//Suprimmer l'animation lors du changement d'activité

            }
        });

        drive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /******************* Changement de page *******************/
                Intent otherActivity = new Intent(getApplicationContext(), Drive.class); //Ouverture d'une nouvelle activité
                startActivity(otherActivity);

                finish();//Fermeture de l'ancienne activité
                overridePendingTransition(0, 0);//Suprimmer l'animation lors du changement d'activité

            }
        });

        messagerie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /******************* Changement de page *******************/
                Intent otherActivity = new Intent(getApplicationContext(), Messagerie.class); //Ouverture d'une nouvelle activité
                startActivity(otherActivity);

                finish();//Fermeture de l'ancienne activité
                overridePendingTransition(0, 0);//Suprimmer l'animation lors du changement d'activité

            }
        });
    }

    /******************* Gestion du retour en arrière *******************/
    @Override
    public void onBackPressed() {

        /******************* Changement de page *******************/
        Intent otherActivity = new Intent(getApplicationContext(), Information.class); //Ouverture d'une nouvelle activité
        startActivity(otherActivity);


        finish();//Fermeture de l'ancienne activité
        overridePendingTransition(0, 0);//Suprimmer l'animation lors du changement d'activité

    }


    public class TelechargementCalendrier extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            //On réinitialise les tables de la base de données ...
            //databaseManager.deleteAllMatieres();
            //databaseManager.deleteAllNotes();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String codeRss = codeSource(); //On récupère le code rss

            decodage(codeRss); //On va selectionner les informations dans le text qui nous interressent

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //On affiche les notes
            affichageCalendrier();

        }
    }

    public String codeSource() {

        String codeRss = "";
        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.connect();

            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());

            byte[] bytes = new byte[1024];
            int tmp;
            while ((tmp = bis.read(bytes)) != -1) {
                String chaine = new String(bytes, 0, tmp);
                codeRss += chaine;
            }

            conn.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return codeRss;
    }

    public void decodage(String codeRss){
            //Parcer le code pour récupérer les informations importantes/utiles

            //stockageCalendrier(...);
    }

    public void stockageNote(){

        //Faire les insertion dans la base de donnée
    }

    public void affichageCalendrier(){
        //Gérer l'affichage avec le linéare layout
    }
}