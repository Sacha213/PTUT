package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Calendrier extends AppCompatActivity {

    /******************* Attribut *******************/
    private Menu menu;

    private Button deconnexion;
    private TextView datecourante;
    private RelativeLayout layoutFront;
    private RelativeLayout layoutBack;
    private ImageView flecheGauche;
    private ImageView flecheDroite;

    private Calendar dateAffiche;

    //Base de données
    private DatabaseManager databaseManager;

    private FirebaseAuth mAuth;



    private String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendrier);

        /******************* Initialisation des variables *******************/
        this.deconnexion = findViewById(R.id.boutonDeconnexion);

        this.layoutFront = findViewById(R.id.layoutCalendrierFront);
        this.layoutBack = findViewById(R.id.layoutCalendrierBack);
        this.datecourante = findViewById(R.id.textjour);
        this.flecheDroite = findViewById(R.id.flechedroite);
        this.flecheGauche = findViewById(R.id.flechegauche);

        databaseManager = new DatabaseManager(this);
        dateAffiche = Calendar.getInstance();

        this.menu = new Menu(this,databaseManager);

        url = databaseManager.getLienCalendrier();

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE); //Gestionnaire connexion réseau
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo(); //Information du réseau

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        if (networkInfo != null) { //On vérifie qu'il y a une connexion à internet

             TelechargementCalendrier cal = new TelechargementCalendrier();
             cal.execute();
        }
        else {
            affichageCalendrier();//On affiche le calendrier de la bd sans les actualisé
        }

        /******************* Mise en place d'écouteur *******************/
        flecheGauche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //On diminue le jours de 1 et on apelle l'affichage
                dateAffiche.add(Calendar.DATE,-1);
                layoutBack.removeAllViews();
                layoutFront.removeAllViews();
                affichageCalendrier();
            }
        });

        /******************* Mise en place d'écouteur *******************/
        flecheDroite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //On augmente le jours de 1 et on apelle l'affichage
                dateAffiche.add(Calendar.DATE,1);
                layoutBack.removeAllViews();
                layoutFront.removeAllViews();
                affichageCalendrier();

            }
        });


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


    }

    /******************* Gestion du retour en arrière *******************/
    @Override
    public void onBackPressed() {

        /******************* Changement de page *******************/
        Intent otherActivity = new Intent(getApplicationContext(), Information.class); //Ouverture d'une nouvelle activité
        startActivity(otherActivity);

        //On ferme la database
        databaseManager.close();


        finish();//Fermeture de l'ancienne activité
        overridePendingTransition(0, 0);//Suprimmer l'animation lors du changement d'activité

    }


    public class TelechargementCalendrier extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            //On réinitialise la table calendrier de la base de données
            databaseManager.deleteAllCours();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            String codeCal = codeSource(); //On récupère le code du fichier calendrier

            decodage(codeCal); //On va selectionner les informations dans le text qui nous interressent

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //On affiche les cours
            affichageCalendrier();

        }

        public String codeSource() {

            String codeCal = "";
            HttpURLConnection conn = null;

            try {
                conn = (HttpURLConnection) new URL(url).openConnection();
                conn.connect();

                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());

                byte[] bytes = new byte[1024];
                int tmp;
                while ((tmp = bis.read(bytes)) != -1) {
                    String chaine = new String(bytes, 0, tmp);
                    codeCal += chaine;
                }

                //On ferme la connexion
                conn.disconnect();

            } catch (IOException e) {
                e.printStackTrace();
            }


            return codeCal;
        }

        public void decodage(String codeCal){
            //Parcer le code pour récupérer les informations importantes/utiles

            String[] event = codeCal.split("BEGIN:VEVENT");
            for(int i=1;i< event.length; i++){
                // on va parser chaque partie de notre event pour recuperer seulement les informations necessaires
                int debutCours = Integer.parseInt(event[i].split("DTSTART:")[1].substring(9,13));
                int finCours = Integer.parseInt(event[i].split("DTEND:")[1].substring(9,13));
                String nomCours = event[i].split("SUMMARY:")[1].split("LOCATION:")[0];
                String salle = event[i].split("LOCATION:")[1].split("DESCRIPTION:")[0];
                String prof = event[i].split("DESCRIPTION:")[1];
                String idCours = event[i].split("DTSTART:")[1].split("DTEND:")[0];
                String date = event[i].split("DTSTART:")[1].substring(0,8);


                //on ajoute 100 equivalent a 1h a chauqe horaire car basé sur le fuseau horaire anglais
                debutCours +=100;
                finCours +=100;

                //on modifie l'affichage de la date en separant mois jour et année par des /
                String annee = date.substring(0,4);
                String mois = date.substring(4,6);
                String jour = date.substring(6,8);
                date = jour+"/"+mois+"/"+annee;

                //Insertion dans la base de donnée
                databaseManager.insertCours(idCours,debutCours,finCours,date,nomCours,salle,prof);
            }


        }


    }


    public void affichageCalendrier() {

        /******************* affichage date *******************/

        SimpleDateFormat formateur = new SimpleDateFormat("dd/MM/yyyy");
        String strDateAffiche= formateur.format(dateAffiche.getTime());
        datecourante.setText(strDateAffiche);


        String[] tabId = databaseManager.getCours(strDateAffiche);

        int position = 0;

        //Parcourir toutes le minutes de la journée de 8h à 20h
        for(int i=8;i<=20;i++){

            LinearLayout lCalHori = new LinearLayout(getApplicationContext());
            lCalHori.setOrientation(LinearLayout.HORIZONTAL);
            lCalHori.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams paramslayoutCal = new LinearLayout.LayoutParams(layoutBack.getWidth(), 150);
            paramslayoutCal.setMargins(0, position, 0, 0);
            lCalHori.setLayoutParams(paramslayoutCal);

            //On affiche l'heure tt les deux Heure
            TextView heure = new TextView(getApplicationContext());
            if(i<10){
                heure.setText("0"+String.valueOf(i).substring(0,1)+":00 ");
            }else{
                heure.setText(String.valueOf(i).substring(0,2)+":00 ");
            }
            heure.setTextSize(20);

            if(i%2!=0){  //Si c pas une heure paire on ne l'affiche pas zeubi
                heure.setVisibility(View.INVISIBLE);
            }
            lCalHori.addView(heure);

            //Ajout du trait
            View trait = new View(getApplicationContext());
            trait.setBackgroundColor(Color.GRAY);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(layoutBack.getWidth(), 3);
            trait.setLayoutParams(params);
            lCalHori.addView(trait);

            layoutBack.addView(lCalHori);

            position+=60*2;



        }

        //On affiche les cours

        for(String id : tabId){
            int duree = databaseManager.getHFIN(id)-databaseManager.getHDEB(id);
            duree = conversionHeureMinute(duree)*2;
            position = conversionHeureMinute(databaseManager.getHDEB(id)-800) *2;
            LinearLayout blockCours = new LinearLayout(getApplicationContext());
            blockCours.setOrientation(LinearLayout.VERTICAL);
            blockCours.setGravity(Gravity.CENTER_VERTICAL);

            TextView blocktitre = new TextView(getApplicationContext());
            TextView blockprof = new TextView(getApplicationContext());
            TextView blocksalle = new TextView(getApplicationContext());

            blocktitre.setText(databaseManager.getNomCours(id));
            blockprof.setText(databaseManager.getProf(id));
            blocksalle.setText(databaseManager.getSalle(id));

            blockCours.setBackgroundColor(getResources().getColor(R.color.vert_claire));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(layoutFront.getWidth(), duree);
            params.setMargins(0, position, 0, 0);

            blockCours.setLayoutParams(params);
            blockCours.addView(blocktitre);
            blockCours.addView(blockprof);
            blockCours.addView(blocksalle);
            layoutFront.addView(blockCours);

        }



    }

    public int conversionHeureMinute(int duree){
        int convertion;

        convertion = duree%100 ;//On récupère les minute (les deux chiffre à droite)

        convertion += (duree/100)*60;//On récupere les heure qu'on convertie en minutes

        return convertion;
    }
}